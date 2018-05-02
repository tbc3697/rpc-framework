package pub.tbc.rpc.framework.serializer.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import pub.tbc.rpc.framework.serializer.Serialization;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tbc on 2018/4/16.
 */
public class ProtostuffSerializer<T> implements Serialization<T> {
    private static Map<Class<?>, Schema<?>> cacheSchema = new ConcurrentHashMap<>();
    private static Objenesis objenesis = new ObjenesisStd(true);


    private static <T> Schema<T> getSchema(Class<T> c) {
        Schema<T> schema = (Schema<T>) cacheSchema.get(c);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(c);
            cacheSchema.put(c, schema);
        }
        return schema;
    }

    @Override
    public byte[] serialize(T object) {
        Class<T> tClass = (Class<T>) object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(tClass);
            return ProtobufIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public T deserialize(byte[] data, Class<T> glass) {
        try {
            T message = objenesis.newInstance(glass);
            Schema<T> schema = getSchema(glass);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
