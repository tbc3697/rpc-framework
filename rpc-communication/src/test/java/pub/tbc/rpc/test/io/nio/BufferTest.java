package pub.tbc.rpc.test.io.nio;

import lombok.extern.slf4j.Slf4j;

import java.nio.CharBuffer;

/**
 * Created by tbc on 2018/4/18.
 */
@Slf4j
public class BufferTest {
    public static void main(String[] args) {
        CharBuffer buffer = CharBuffer.allocate(10);
        log.info("Capacity: {}", buffer.capacity());
        log.info("limit: {}", buffer.limit());
        log.info("Position: {}", buffer.position());
        log.info("remaining(可读个数): {}", buffer.remaining());
        log.info("设置limit为6：");
        buffer.limit(6);
        log.info("limit: {}", buffer.limit());
        log.info("position: {}", buffer.position());
        log.info("remaining: {}", buffer.remaining());
        log.info("设置position为2: ");
        buffer.position(2);
        log.info("position: {}", buffer.position());
        log.info("remaining: {}", buffer.remaining());
        System.out.println(buffer);


    }
}
