package pub.tbc.rpc.framework.serializer.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import pub.tbc.rpc.framework.serializer.Serialization;
import pub.tbc.toolkit.core.collect.MapBuilder;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

/**
 * Created by tbc on 2018/4/17.
 */
public class KryoSerializer<T> implements Serialization<T> {
    @Override
    public byte[] serialize(T object) {
        try (Output output = new Output(1024)) {
            Kryo kryo = new Kryo();
            kryo.writeClassAndObject(output, object);
            return output.toBytes();
        }
    }

    @Override
    public T deserialize(byte[] data, Class<T> glass) {
        try (Input input = new Input(new ByteArrayInputStream(data))) {
            Kryo kryo = new Kryo();
            return (T) kryo.readClassAndObject(input);
//            return kryo.readObject(input, glass);
        }
    }

    public static void main(String[] args){
        HashMap<String, String> map = new MapBuilder<String, String>().put("a","aa").build();
        Serialization<HashMap> serialization = new KryoSerializer<>();
        byte[] data = serialization.serialize(map);
        System.out.println(data.length);
        System.out.println("---------------------------------------------");
        HashMap<String, String> value = serialization.deserialize(data, HashMap.class);
        System.out.println(value);
    }
}
