package pub.tbc.rpc.remoting.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.framework.serializer.SerializerEngine;
import pub.tbc.rpc.framework.serializer.SerializerType;

import java.util.List;

/**
 * 解码器：将字节数组解码为JAVA对象
 *
 * @author tbc on 2018/4/19.
 */
@Slf4j
public class NettyDecoderHandler extends ByteToMessageDecoder {
    // 解码对象类型Class
    private Class<?> genericClass;
    // 序列化类型
    private SerializerType serializerType;

    public NettyDecoderHandler(Class<?> genericClass, SerializerType serializerType) {
        log.debug("init {}", getClass());
        this.genericClass = genericClass;
        this.serializerType = serializerType;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        log.debug("准备反序列化[解码]...");
        // 获取消息头中标识的消息体字节数组长度
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }

        // 若当前可以获取到的字节数小于实际长度，直接返回，直到当前可读字节数等于实际长度
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // 读取完整的消息体字节数组
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        // 反序列化
        Object object = SerializerEngine.deserialize(data, genericClass, serializerType.getSerializerType());
        out.add(object);
        log.debug("反序列化[解码]完成: {}", object);
    }
}
