package pub.tbc.rpc.framework.serializer;

import pub.tbc.toolkit.core.collect.Maps;

import java.util.Map;

import static pub.tbc.toolkit.core.EmptyUtil.*;
import static pub.tbc.toolkit.core.exception.ExceptionUtil.noException;

/**
 * Created by tbc on 2018/4/16.
 */
public class SerializerEngine {
    public static final Map<SerializerType, Serialization> serializerMap = Maps.newConcurrentHashMap();

    static {
        //
    }

    public static <T> byte[] serialize(T object, String serializerTypeName) {
        SerializerType serializerType = SerializerType.queryByType(serializerTypeName);
        requireNonNull(serializerType, "serializer is null");

        Serialization serializer = serializerMap.get(serializerType);
        requireNonNull(serializer, "serialize error");

        return noException(() -> serializer.serialize(object));
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz, String serializerTypeName) {
        SerializerType serializerType = SerializerType.queryByType(serializerTypeName);
        requireNonNull(serializerType, "serializer is null");

        Serialization serializer = serializerMap.get(serializerType);
        requireNonNull(serializer, "serialize error");

        return noException(() -> serializer.deserialize(data, clazz));
    }
}
