package blackgt.rpc.codec;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.enums.RpcPackageType;
import blackgt.rpc.serializer.defaultSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author blackgt
 * @Date 2022/11/21 19:47
 * @Version 1.0
 * 说明 ：默认编码器
 */
public class defaultEncoder extends MessageToByteEncoder {
    //魔数
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final defaultSerializer serializer;

    public defaultEncoder(defaultSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        //写入魔数
        byteBuf.writeInt(MAGIC_NUMBER);
        //写入类型
        if(o instanceof RpcRequest){
            //如果是请求类型，则写入0
            byteBuf.writeInt(RpcPackageType.REQUEST_TYPE.getTypeCode());
        }else{
            //如果是回复类型，则写入1
            byteBuf.writeInt(RpcPackageType.RESPONSE_TYPE.getTypeCode());
        }
        //写入序列化对象标识
        byteBuf.writeInt(serializer.getCode());
        //将对象序列化
        byte[] serializerByte = this.serializer.serializer(o);
        //写入被序列化对象的长度，以解决粘包拆包问题
        byteBuf.writeInt(serializerByte.length);
        //写入被序列化的值
        byteBuf.writeBytes(serializerByte);

    }
}
