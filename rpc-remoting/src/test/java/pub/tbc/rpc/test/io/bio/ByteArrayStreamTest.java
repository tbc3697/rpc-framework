package pub.tbc.rpc.test.io.bio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by tbc on 2018/4/18.
 */
public class ByteArrayStreamTest {
    public static void main(String[] args) throws IOException {
        run(() -> byteArrayOutputStream());


    }


    private static long start;

    public static void before() {
        start = System.currentTimeMillis();
        System.out.println("start time : " + start);
    }

    public static void after() {
        System.out.println("total: " + (System.currentTimeMillis() - start));
    }

    public static void run(Runnable runnable) {
        before();
        runnable.run();
        after();
    }

    public static void byteArrayOutputStream() {
        String content = "你好, java Blocking I/O," +
                "你好, java Blocking I/O," +
                "你好, java Blocking I/O," +
                "你好, java Blocking I/O,";
        byte[] inputBytes = content.getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);

        byte[] bytes = new byte[1];
        int size;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            while ((size = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, size);
            }
            System.out.println(out.toString("utf-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
