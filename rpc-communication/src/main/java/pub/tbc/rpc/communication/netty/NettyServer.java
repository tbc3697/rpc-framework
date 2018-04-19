package pub.tbc.rpc.communication.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import pub.tbc.rpc.common.helper.PropertyConfigeHelper;
import pub.tbc.rpc.framework.serializer.SerializerType;

import static pub.tbc.toolkit.core.EmptyUtil.nonNull;

/**
 * Created by tbc on 2018/4/19.
 */
public class NettyServer {
    private static NettyServer nettyServer = new NettyServer();

    // boss
    private EventLoopGroup bossGroup;
    // worker
    private EventLoopGroup workerGroup;
    //
    private SerializerType serializerType = PropertyConfigeHelper.getSerializeType();

    public static NettyServer singleton() {
        return nettyServer;
    }


    public void start(int port) {
        synchronized (NettyServer.class) {
            if (checkStarted()) return;
            //
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            //
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(channelInitializer());

            try {
                b.bind(port).sync().channel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private ChannelInitializer channelInitializer() {
        return null;
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
