package pub.tbc.rpc.framework.serializer;

import java.util.Arrays;

/**
 * Created by tbc on 2018/4/16.
 */
public enum SerializerType {
    DEFAULT_JAVA_SERIALIZER("DefaultJavaSerializer");

    private String serializerType;

    SerializerType(String serializerType) {
        this.serializerType = serializerType;
    }

    public String getSerializerType() {
        return serializerType;
    }

    public static SerializerType queryByType(String serializerTypeName) {
        if (serializerTypeName == null || serializerTypeName.isEmpty()) {
            return null;
        }
        return Arrays.stream(SerializerType.values())
                .filter(t -> t.serializerType.equals(serializerTypeName))
                .findFirst()
                .orElse(null);
    }


}
