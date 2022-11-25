package blackgt.rpc.codec;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.enums.RpcPackageType;
import blackgt.rpc.exceptions.RpcException;
import blackgt.rpc.serializer.defaultSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author blackgt
 * @Date 2022/11/22 13:38
 * @Version 1.0
 * 说明 ：默认解码器
 */
public class defaultDecoder extends ReplayingDecoder {
    private static final Logger logger = LoggerFactory.getLogger(defaultDecoder.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magic = byteBuf.readInt();
        if(magic != MAGIC_NUMBER){
            logger.error("该包为非协议包: {}",magic);
            throw new RpcException(RpcErrorMessageEnums.UNKNOWN_PROTOCOL);
        }
        //读取请求类型
        int rpcPackageType = byteBuf.readInt();
        Class<?> rpcPackageClass;
        //读取数据包类型（Request/Response）
        if(rpcPackageType == RpcPackageType.REQUEST_TYPE.getTypeCode()){
            rpcPackageClass = RpcRequest.class;
        }else if(rpcPackageType == RpcPackageType.RESPONSE_TYPE.getTypeCode()){
            rpcPackageClass = RpcResponse.class;
        }else{
            logger.error("未识别数据包类型{}",rpcPackageType);
            throw new RpcException(RpcErrorMessageEnums.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerCode = byteBuf.readInt();
        //根据包内容获取序列化对象
        defaultSerializer serializer = defaultSerializer.getByCode(serializerCode);
        if(serializer ==null){
            logger.error("未能识别序列化器 {}",serializerCode);
            throw new RpcException(RpcErrorMessageEnums.UNKNOWN_SERIALIZER);
        }
        //读取包长度
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object o = serializer.deSerializer(bytes, rpcPackageClass);
        list.add(o);
    }
}
