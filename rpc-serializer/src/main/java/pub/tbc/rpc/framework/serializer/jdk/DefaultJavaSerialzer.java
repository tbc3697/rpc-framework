package pub.tbc.rpc.framework.serializer.jdk;

import pub.tbc.rpc.framework.serializer.Serialization;

import java.io.*;

/**
 * Created by tbc on 2018/4/15.
 */
public class DefaultJavaSerialzer implements Serialization {

    @Override
    public <T> byte[] serialize(T object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream)) {
            out.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> glass) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try (ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (T) inputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
