package pub.tbc.rpc.communication.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.common.model.RevokerResponseHolder;
import pub.tbc.rpc.common.model.RpcRequest;
import pub.tbc.rpc.common.model.RpcResponse;
import pub.tbc.toolkit.core.EmptyUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Netty请求发起调用
 */
@Slf4j
public class RevokerServiceCallable implements Callable<RpcResponse> {
    private Channel channel;
    private InetSocketAddress socketAddress;
    private RpcRequest request;

    public static RevokerServiceCallable of(InetSocketAddress socketAddress, RpcRequest request) {
        return new RevokerServiceCallable(socketAddress, request);
    }

    public RevokerServiceCallable(InetSocketAddress socketAddress, RpcRequest request) {
        this.socketAddress = socketAddress;
        this.request = request;
    }


    @Override
    public RpcResponse call() throws Exception {
        // 初始化返回结果容器， 将本次调用的唯一标识作为key存入返回结果的Map
        RevokerResponseHolder.initResponseData(request.getUniqueKey());

        // 根据本地调用服务提供者地址获取对应的Netty通道channel队列
        ArrayBlockingQueue<Channel> blockingQueue = NettyChannelPoolFactory.instance().acquire(socketAddress);

        try {
            ChannelFuture channelFuture = channel.writeAndFlush(request);
            channelFuture.syncUninterruptibly();

            // 从返回结果容器中获取返回结果，同时设置等待超时时间为invokerTimeout
            return RevokerResponseHolder.getValue(request.getUniqueKey(), request.getInvokeTimeout());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 本次调用完成后，channel重新入队，以便下次复用
            NettyChannelPoolFactory.instance().release(blockingQueue, channel, socketAddress);
        }
    }


    private void getChannel() {
        // 根据本地调用服务提供者地址获取对应的Netty通道channel队列
        ArrayBlockingQueue<Channel> blockingQueue = NettyChannelPoolFactory.instance().acquire(socketAddress);

        if (EmptyUtil.isNull(channel)) {
            //
            try {
                channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);

                // 若获取的channel通道已经不可用，则重新获取一个
                while (!channel.isOpen() || !channel.isActive() || !channel.isWritable()) {
                    log.warn("----------retry get new Channel------------");
                    channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);

                    // 如果队列中没有可用的channel，则重新注册一个
                    if (EmptyUtil.isNull(channel)) {
                        channel = NettyChannelPoolFactory.instance().registerChannel(socketAddress);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
