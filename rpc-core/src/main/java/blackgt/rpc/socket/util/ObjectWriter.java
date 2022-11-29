package blackgt.rpc.socket.util;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.enums.RpcPackageType;
import blackgt.rpc.serializer.defaultSerializer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author blackgt
 * @Date 2022/11/29 15:06
 * @Version 1.0
 * 说明 ：
 */
public class ObjectWriter {
    //魔数
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    public static void writeObject(OutputStream outputStream, Object obj, defaultSerializer serializer) throws IOException {
        if(obj instanceof RpcRequest){
            outputStream.write(intToBytes(RpcPackageType.REQUEST_TYPE.getTypeCode()));
        }else {
            outputStream.write(intToBytes(RpcPackageType.RESPONSE_TYPE.getTypeCode()));
        }
        outputStream.write(intToBytes(serializer.getCode()));
        byte[] bytes = serializer.serializer(obj);
        outputStream.write(intToBytes(bytes.length));
        outputStream.write(bytes);
        outputStream.flush();

    }

    private static byte[] intToBytes(int value){
        byte[] src = new byte[4];
        src[0] = (byte)((value>>24) & 0xFF);
        src[1] = (byte)((value>>16) & 0xFF);
        src[2] = (byte)((value>>8) & 0xFF);
        src[3] = (byte)(value & 0xFF);
        return src;
    }
}
