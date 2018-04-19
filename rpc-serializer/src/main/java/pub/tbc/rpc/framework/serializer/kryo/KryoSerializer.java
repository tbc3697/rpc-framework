package pub.tbc.rpc.framework.serializer.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import pub.tbc.rpc.framework.serializer.Serialization;

/**
 * Created by tbc on 2018/4/17.
 */
public class KryoSerializer implements Serialization {
    @Override
    public <T> byte[] serialize(T object) {
        try (Output output = new Output(1024)) {
            Kryo kryo = new Kryo();
            kryo.writeObject(output, object);
            return output.toBytes();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> glass) {
        return null;
    }
}
