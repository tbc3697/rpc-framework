package pub.tbc.rpc.test.io.bio;

import pub.tbc.rpc.test.io.Inet;
import pub.tbc.toolkit.core.Closes;

import java.io.*;
import java.net.Socket;

/**
 * Created by tbc on 2018/4/18.
 */
public class BIOEchoClient {
    public static void main(String[] args) {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try (Socket socket = new Socket(Inet.SERVER_IP, Inet.SERVER_PORT)) {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // send
            writer.write("Hello, Blocking I/O.\n");
            writer.flush();

            // receive
            String echo = reader.readLine();
            System.out.println("echo: " + echo);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closes.close(reader, writer);
        }
    }
}
