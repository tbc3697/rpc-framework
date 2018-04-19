package pub.tbc.rpc.test.io.bio;

import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.test.io.Inet;
import pub.tbc.toolkit.core.Closes;
import pub.tbc.toolkit.core.EmptyUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by tbc on 2018/4/18.
 */
@Slf4j
public class BIOEchoServer {
    static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(Inet.SERVER_PORT)) {
            while (true) {
                // use
                Socket socket = server.accept();
                executor.submit(() -> {
                    BufferedWriter writer = null;
                    BufferedReader reader = null;
                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        while (true) {
                            String line = reader.readLine();
                            if (EmptyUtil.isEmpty(line)) {
                                break;
                            } else {
                                log.info("receive message - msg: [{}], from: [{}]", line, socket.getRemoteSocketAddress());
                                log.info("thread pool monitor - thread: [{}], task:[{}]", executor.getPoolSize(), executor.getTaskCount());
                            }
                            writer.write(line + "\n");
                            writer.flush();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        Closes.close(writer, reader);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
