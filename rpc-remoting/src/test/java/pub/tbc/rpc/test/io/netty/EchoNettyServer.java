package pub.tbc.rpc.test.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import pub.tbc.rpc.test.io.Inet;

import java.nio.charset.StandardCharsets;

/**
 * Created by tbc on 2018/4/19.
 */
public class EchoNettyServer {
    public static void main(String[] args) {
        new EchoNettyServer().bind(Inet.SERVER_PORT);
    }

    public void bind(int port) {
        //
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);

        try {
            //
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    //
                    .channel(NioServerSocketChannel.class)
                    // TCP参数，连接请求的最大队列
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 设置I/O事件处理类，用来处理消息编解码及业务逻辑
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            // 绑定端口，同步等待成功
            ChannelFuture future = b.bind(port).sync();
            System.out.println("server startup ...");
            // 等待服务端监听端口关闭
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅退出
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    class EchoServerHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);

            //
            String body = new String(req, StandardCharsets.UTF_8);
            System.out.println("receive data from client: " + body);

            //
            ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
            ctx.write(resp);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
            context.close();
        }

        // 将发送缓冲区中的消息全部写入SocketChannel中
        @Override
        public void channelReadComplete(ChannelHandlerContext context) throws Exception {
            context.flush();
        }
    }
}
