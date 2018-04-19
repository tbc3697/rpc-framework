package pub.tbc.rpc.framework.serializer;

/**
 * 序列化接口
 */
public interface Serialization {

    /**
     * 序列化
     *
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T object);

    /**
     * 反序列化
     *
     * @param data
     * @param glass
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> glass);
}
