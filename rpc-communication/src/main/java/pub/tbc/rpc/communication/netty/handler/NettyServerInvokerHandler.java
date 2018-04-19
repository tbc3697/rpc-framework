package pub.tbc.rpc.communication.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import pub.tbc.rpc.common.RpcRequeset;

/**
 * Created by tbc on 2018/4/19.
 */
public class NettyServerInvokerHandler extends SimpleChannelInboundHandler<RpcRequeset> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequeset msg) throws Exception {

    }
}
