package pub.tbc.rpc.framework.serializer.xml;

import pub.tbc.rpc.framework.serializer.Serialization;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by tbc on 2018/4/16.
 */
public class XmlSerializerByJava implements Serialization {
    @Override
    public <T> byte[] serialize(T object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (XMLEncoder xmlEncoder = new XMLEncoder(byteArrayOutputStream, "utf-8", true, 0)) {
            xmlEncoder.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> glass) {
        try (XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(data))) {
            return (T) xmlDecoder.readObject();
        }
    }
}
