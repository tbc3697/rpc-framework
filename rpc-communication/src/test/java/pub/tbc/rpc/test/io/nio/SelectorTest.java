package pub.tbc.rpc.test.io.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

/**
 * Created by tbc on 2018/4/19.
 */
public class SelectorTest {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_READ);
        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) continue;

            Set<SelectionKey> selectKeys = selector.selectedKeys();
            selectKeys.forEach(selectionKey -> {
                if (selectionKey.isAcceptable()) {
                    // connection accepted event
                } else if (selectionKey.isConnectable()) {
                    //
                } else if (selectionKey.isReadable()) {
                    //
                } else if (selectionKey.isWritable()) {

                }
                selectKeys.remove(selectionKey);
            });

        }
    }
}
