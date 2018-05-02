package pub.tbc.rpc.remoting.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.framework.serializer.SerializerEngine;
import pub.tbc.rpc.framework.serializer.SerializerType;

/**
 * Created by tbc on 2018/4/19.
 */
@Slf4j
public class NettyEncoderHandler extends MessageToByteEncoder {
    private SerializerType serializerType;

    public NettyEncoderHandler(SerializerType serializerType) {
        log.debug("init {}", getClass());
        this.serializerType = serializerType;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        log.debug("准备序列化[编码]: {}", msg);
        // 序列化
        byte[] data = SerializerEngine.serialize(msg, serializerType.getSerializerType());

        // 写消息头（内容为消息体长度）
        out.writeInt(data.length);

        // 写消息体（实际内容）
        out.writeBytes(data);
    }
}
