package pub.tbc.rpc.remoting.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.common.model.RpcRequest;
import pub.tbc.rpc.common.model.RpcResponse;
import pub.tbc.rpc.registry.IRegisterCenter4Provider;
import pub.tbc.rpc.registry.zk.RegisterCenter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 实际业务处理器，负责查找实际服务提供对象，并进行方法调用
 *
 * @author tbc on 2018/5/6.
 */
@Slf4j
public class NettyServerInvokerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    public NettyServerInvokerHandler() {
        log.debug("init {}", getClass());
    }

    /**
     * 方法名匹配，并且方法参数都为空或者方法参数完全匹配
     */
    private boolean matchMethod(Method method, String methodName, Class<?>... methodParameters) {
        if (methodName.equals(method.getName())) {
            if (methodParameters.length == 0 && method.getParameterTypes().length == 0) {
                return true;
            }
            return Arrays.equals(method.getParameterTypes(), methodParameters);
        }
        return false;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) {
        log.debug("收到请求，来自：{}", ctx.channel().remoteAddress());
        if (ctx.channel().isWritable()) {
            // 从服务调用对象里获取服务提供者信息
            ProviderService metaDataModel = request.getProviderService();
            long consumeTimeOut = request.getInvokeTimeout();
            String methodName = request.getInvokedMethodName();

            String serviceKey = metaDataModel.getServiceItf().getName();

            // 服务注册中心
            IRegisterCenter4Provider registerCenter4Provider = RegisterCenter.singleton();
            List<ProviderService> localProviderCaches = registerCenter4Provider.getProviderServiceMap().get(serviceKey);

            ProviderService localProviderCache = localProviderCaches.stream()
                    //只用名称过滤的话存在问题，若服务中存在重载方法无法区分
                    .filter(lpc -> matchMethod(lpc.getServiceMethod(), methodName, request.getMethodParametersType()))
                    .findFirst()
                    .get();
            Object serviceObject = localProviderCache.getServiceObject();

            // 利用反射发起调用
            Method method = localProviderCache.getServiceMethod();
            Object result = null;
            boolean acquire = false;

            try {
                // 反射调用实际提供者方法，结果赋给result变量
                result = method.invoke(serviceObject, request.getArgs());
            } catch (Exception e) {
                result = e;
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
