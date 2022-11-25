package blackgt.rpc.serializer;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.enums.SerializerCode;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Author blackgt
 * @Date 2022/11/21 20:51
 * @Version 1.0
 * 说明 ：
 */
public class JsonSerializer implements defaultSerializer{
    private static final Logger logger = LoggerFactory.getLogger(defaultSerializer.class);

    @Override
    public int getCode() {
        //返回序列化/反序列化器标识
        return SerializerCode.valueOf("JACKSON").getCode();
    }

    //jackson的对象映射器
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serializer(Object res) {
        try{
            return objectMapper.writeValueAsBytes(res);
        } catch (JsonProcessingException e) {
            logger.error("Jackson序列化时发生错误:{}",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deSerializer(byte[] bytes, Class<?> clazz) {
        try {
            Object readValue = objectMapper.readValue(bytes, clazz);
            if(readValue instanceof RpcRequest){
                readValue = requestHelper(readValue);
            }
            return readValue;
        } catch (IOException e) {
            logger.error("Jackson反序列化时发生异常:{}",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    //判断是否为原实例类型
    private Object requestHelper(Object object) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) object;
        for (int i = 0; i < rpcRequest.getMethodParameterType().length; i++) {
            Class<?> aClass = rpcRequest.getMethodParameterType()[i];
            //isAssignableFrom:确定待调用参数类型是不是继承来自于另一个父类,或者判断是否是相同类
            //如果不是
            if(!aClass.isAssignableFrom(rpcRequest.getMethodParameters()[i].getClass())){
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getMethodParameters()[i]);
                rpcRequest.getMethodParameters()[i] = objectMapper.readValue(bytes, aClass);
            }
        }
        return rpcRequest;
    }
}
