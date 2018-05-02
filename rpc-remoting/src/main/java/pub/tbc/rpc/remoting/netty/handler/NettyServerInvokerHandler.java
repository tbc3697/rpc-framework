package pub.tbc.rpc.remoting.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.common.model.RpcRequest;
import pub.tbc.rpc.common.model.RpcResponse;
import pub.tbc.rpc.registry.IRegisterCenter4Provider;
import pub.tbc.rpc.registry.zk.RegisterCenter;
import pub.tbc.toolkit.core.EmptyUtil;
import pub.tbc.toolkit.core.collect.Maps;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by tbc on 2018/4/19.
 */
@Slf4j
public class NettyServerInvokerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    {
        log.debug("init {}", getClass());
    }

    // 服务端限流
    private static final Map<String, Semaphore> serviceKeySemaphoreMap = Maps.newConcurrentHashMap();


    private Semaphore getSemaphore(ProviderService metaDataModel) {
        //
        String serviceKey = metaDataModel.getServiceItf().getName();
        // 获取限流工具类
        int workerThread = metaDataModel.getWorkerThreads();
        Semaphore semaphore = serviceKeySemaphoreMap.get(serviceKey);
        // 初始化流控基础设施semaphore
        if (EmptyUtil.isNull(semaphore)) {
            synchronized (serviceKeySemaphoreMap) {
                semaphore = serviceKeySemaphoreMap.get(serviceKey);
                if (EmptyUtil.isNull(semaphore)) {
                    semaphore = new Semaphore(workerThread);
                    serviceKeySemaphoreMap.put(serviceKey, semaphore);
                }
            }
        }
        return semaphore;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        if (ctx.channel().isWritable()) {
            // 从服务调用对象里获取服务提供者信息
            ProviderService metaDataModel = request.getProviderService();
            long consumeTimeOut = request.getInvokeTimeout();

            String serviceKey = metaDataModel.getServiceItf().getName();

            Semaphore semaphore = getSemaphore(metaDataModel);

            // 服务注册中心
            IRegisterCenter4Provider registerCenter4Provider = RegisterCenter.singleton();
            List<ProviderService> localProviderCaches = registerCenter4Provider.getProviderServiceMap().get(serviceKey);

            ProviderService localProviderCache = localProviderCaches.stream()
                    .filter(lpc -> metaDataModel.equals(lpc.getServiceMethod().getName()))
                    .findFirst()
                    .get();
            Object serviceObject = localProviderCache.getServiceObject();

            // 利用反射发起调用
            Method method = localProviderCache.getServiceMethod();
            Object result = null;
            boolean acquire = false;

            try {
                // 利用semaphore实现限流
                acquire = semaphore.tryAcquire(consumeTimeOut, TimeUnit.MILLISECONDS);
                if (acquire) {
                    // 反射调用实际提供者方法，结果赋给result变量
                    result = method.invoke(serviceObject, request.getArgs());
                }
            } catch (Exception e) {
                result = e;
            } finally {
                if (acquire) {
                    semaphore.release();
                }
            }

            // 根据服务调用结果组装服务响应对象
            RpcResponse response = RpcResponse.builder()
                    .invokeTimeout(consumeTimeOut)
                    .uniqueKey(request.getUniqueKey())
                    .result(result)
                    .build();

            // 将服务响应对象写入缓冲区，发送到消费端
            ctx.writeAndFlush(response);
        } else {
            log.error("---------- channel closed! ------------");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        ctx.close();
    }
}
