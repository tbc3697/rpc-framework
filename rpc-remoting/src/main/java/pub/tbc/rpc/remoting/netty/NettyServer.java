package pub.tbc.rpc.remoting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.common.helper.RpcConfigHelper;
import pub.tbc.rpc.common.model.RpcResponse;
import pub.tbc.rpc.framework.serializer.SerializerType;
import pub.tbc.rpc.remoting.netty.handler.NettyServerInvokerHandler;
import pub.tbc.rpc.remoting.netty.handler.NettyServerSemaphoreHandler;
import pub.tbc.rpc.remoting.netty.handler.codec.NettyDecoderHandler;
import pub.tbc.rpc.remoting.netty.handler.codec.NettyEncoderHandler;
import pub.tbc.toolkit.core.thread.ExecutorFactory;

import static pub.tbc.toolkit.core.EmptyUtil.nonNull;

/**
 * Created by tbc on 2018/4/19.
 */
@Slf4j
public class NettyServer {
    private static NettyServer nettyServer = new NettyServer();

    // boss
    private EventLoopGroup bossGroup;
    // worker
    private EventLoopGroup workerGroup;
    //
    private SerializerType serializerType = RpcConfigHelper.getSerializeType();

    public static NettyServer singleton() {
        return nettyServer;
    }


    private int workerThreadCount() {
        //
        int cpu2 = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));

        cpu2 = 4;

        return cpu2;
    }


    // 服务有多个，每发布一个服务都要调用该方法，但netty-server只要启动一次，用同步关键字保护，并调用checkStarted方法检查是否已启动
    public void start(int port) {
        synchronized (NettyServer.class) {
            if (checkStarted()) return;
            log.info("Netty Server starting...");
            bossGroup = new NioEventLoopGroup(1, ExecutorFactory.createThreadFactory("boss"));
            workerGroup = new NioEventLoopGroup(workerThreadCount(), ExecutorFactory.createThreadFactory("worker-%d"));

            //
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(handlerInitializer());

            try {
                log.debug("netty server bind port: {}", port);
                b.bind(port).sync().channel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Netty Server started...");

        }
    }

    private ChannelInitializer handlerInitializer() {
        return new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) {
                log.debug("类: {}, 方法: {}", getClass(), Thread.currentThread().getStackTrace()[1].getMethodName());
                log.debug("pipeline: {}", ch.pipeline().getClass().getName() + "@" + Integer.toString(hashCode()));
                ch.pipeline()
                        .addLast("RpcDecoder", new NettyDecoderHandler(RpcResponse.class, serializerType))
                        .addLast("RpcEncoder", new NettyEncoderHandler(serializerType))
                        .addLast("SemaphoreHandler", new NettyServerSemaphoreHandler())
                        .addLast("ServerInvokerHandler", new NettyServerInvokerHandler());
            }
        };
    }

    private boolean checkStarted() {
        synchronized (NettyServer.class) {
            // 若不为空，说明有其它线程已启动或正在启动服务
            if (nonNull(bossGroup) || nonNull(workerGroup)) {
                return true;
            }
            return false;
        }
    }
}
