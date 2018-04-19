package pub.tbc.io.test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by tbc on 2018/4/17.
 */
public class A extends Output {
    public A(int capacity) {
        super(capacity);
    }

    public void close() throws KryoException {
        System.out.println("invoker close method ======================");
        super.close();
    }

    public static <T> byte[] serialize(T object) {
        try (A output = new A(2048)) {
            Kryo kryo = new Kryo();
            kryo.writeObject(output, object);
            return output.toBytes();
        }
    }

    public static void main(String[] args) {
        String ss = "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "abcdefghijklmnopqrstuvwxyz\n";
        byte[] arrays = serialize(ss);
        System.out.println(new String(arrays));
    }
}
