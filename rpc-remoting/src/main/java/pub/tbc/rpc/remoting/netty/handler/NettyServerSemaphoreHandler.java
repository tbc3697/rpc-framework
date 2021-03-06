package pub.tbc.rpc.remoting.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.common.model.RpcRequest;
import pub.tbc.rpc.common.model.RpcResponse;
import pub.tbc.toolkit.core.EmptyUtil;
import pub.tbc.toolkit.core.collect.Maps;

import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 限流处理器
 */
@Slf4j
public class NettyServerSemaphoreHandler extends SimpleChannelInboundHandler<RpcRequest> {

    public NettyServerSemaphoreHandler() {
        log.debug("init {}", getClass());
    }

    // 服务端限流
    private static final Map<String, Semaphore> serviceKeySemaphoreMap = Maps.newConcurrentHashMap();

    private Semaphore initSemaphore(String serviceKey, int workerThread) {
        synchronized (serviceKeySemaphoreMap) {
            Semaphore semaphore = serviceKeySemaphoreMap.get(serviceKey);
            if (EmptyUtil.isNull(semaphore)) {
                semaphore = new Semaphore(workerThread);
                serviceKeySemaphoreMap.put(serviceKey, semaphore);
            }
            return semaphore;
        }
    }


    private Semaphore getSemaphore(ProviderService metaDataModel) {
        String serviceKey = metaDataModel.getServiceItf().getName();
        Semaphore semaphore = serviceKeySemaphoreMap.get(serviceKey);
        // 初始化流控基础设施semaphore
        if (EmptyUtil.isNull(semaphore)) {
            return initSemaphore(serviceKey, metaDataModel.getWorkerThreads());
        }
        return semaphore;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) {
        if (ctx.channel().isWritable()) {
            // 从服务调用对象里获取服务提供者信息
            ProviderService metaDataModel = request.getProviderService();
            long consumeTimeOut = request.getInvokeTimeout();

            Semaphore semaphore = getSemaphore(metaDataModel);
            log.debug("semaphore queue length: {}", semaphore.getQueueLength());

            boolean acquire = false;
            Object result = null;
            try {
                // 利用semaphore实现限流
                acquire = semaphore.tryAcquire(consumeTimeOut, TimeUnit.MILLISECONDS);
                if (acquire) {
                    // 往下传，交给真正处理服务端业务调用的处理器NettyServerInvokerHandler
                    ctx.fireChannelRead(request);
                    return;
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Throwable: {}", cause.getClass());
        log.error(cause.getMessage());
        ctx.close();
    }
}
