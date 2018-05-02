package pub.tbc.rpc.framework.serializer.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import pub.tbc.rpc.framework.serializer.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by tbc on 2018/4/17.
 */
public class HessionSerializer<T> implements Serialization<T> {

    @Override
    public byte[] serialize(T object) {
        Objects.requireNonNull(object);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            HessianOutput ho = new HessianOutput(out);
            ho.writeObject(object);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T deserialize(byte[] data, Class<T> glass) {
        Objects.requireNonNull(data);
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        HessianInput hessianInput = new HessianInput(in);
        try {
            return (T) hessianInput.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
