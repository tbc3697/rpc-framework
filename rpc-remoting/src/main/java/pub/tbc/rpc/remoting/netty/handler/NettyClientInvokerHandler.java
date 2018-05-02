package pub.tbc.rpc.remoting.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.common.model.RevokerResponseHolder;
import pub.tbc.rpc.common.model.RpcResponse;

/**
 * Created by tbc on 2018/4/20.
 */
@Slf4j
public class NettyClientInvokerHandler extends SimpleChannelInboundHandler<RpcResponse> {
    {
        log.debug("init {}", getClass());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        // 将netty异步返回的结果存入阻塞队列，以便调用端同步获取
        RevokerResponseHolder.putResultValue(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
