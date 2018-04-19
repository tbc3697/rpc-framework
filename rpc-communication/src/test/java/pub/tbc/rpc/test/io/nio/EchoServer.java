package pub.tbc.rpc.test.io.nio;

import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.test.io.Inet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

import static pub.tbc.toolkit.core.thread.ExecutorFactory.createThreadFactory;
import static pub.tbc.toolkit.core.thread.ExecutorFactory.newFixedThreadPool;

/**
 * Created by tbc on 2018/4/18.
 */
@Slf4j
public class EchoServer {
    private static ExecutorService executor = newFixedThreadPool(10, createThreadFactory("echo-thread-%d"));

    public static void main(String[] args) {
        System.out.println("startup nio blocking server...");
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            if (serverSocketChannel.isOpen()) {
                serverSocketChannel.configureBlocking(true);
                // 网络传输参数
                serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                // bind
                serverSocketChannel.bind(new InetSocketAddress(Inet.SERVER_IP, Inet.SERVER_PORT));

                for (; ; ) {
                    try {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        executor.submit(() -> {
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            try {
                                while (socketChannel.read(buffer) != -1) {
                                    buffer.flip();
                                    socketChannel.write(buffer);
                                    if (buffer.hasRemaining()) {
                                        buffer.compact();
                                    } else {
                                        buffer.clear();
                                    }
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
