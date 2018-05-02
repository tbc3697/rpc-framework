package pub.tbc.rpc.framework.serializer;

/**
 * 序列化接口
 */
public interface Serialization<T> {

    /**
     * 序列化
     */
    byte[] serialize(T object);

    /**
     * 反序列化
     */
    T deserialize(byte[] data, Class<T> glass);
}
