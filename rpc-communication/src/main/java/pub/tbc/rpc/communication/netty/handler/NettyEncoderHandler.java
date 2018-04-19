package pub.tbc.rpc.communication.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import pub.tbc.rpc.framework.serializer.SerializerEngine;
import pub.tbc.rpc.framework.serializer.SerializerType;

/**
 * Created by tbc on 2018/4/19.
 */
public class NettyEncoderHandler extends MessageToByteEncoder {
    private SerializerType serializerType;

    public NettyEncoderHandler(SerializerType serializerType) {
        this.serializerType = serializerType;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 序列化
        byte[] data = SerializerEngine.serialize(msg, serializerType.getSerializerType());

        // 写消息头（内容为消息体长度）
        out.writeInt(data.length);

        // 写消息体（实际内容）
        out.writeBytes(data);
    }
}
