package blackgt.rpc.serializer;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.enums.SerializerCode;
import blackgt.rpc.exceptions.SerializeException;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author blackgt
 * @Date 2022/11/24 20:00
 * @Version 1.0
 * 说明 ：
 */
public class KryoSerializer implements defaultSerializer{
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        //支持循环引用
        kryo.setReferences(true);
        //关闭注册行为
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public byte[] serializer(Object res) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)){

            Kryo kryo = KRYO_THREAD_LOCAL.get();
            kryo.writeObject(output,res);
            KRYO_THREAD_LOCAL.remove();
            return output.toBytes();
        }catch (Exception e){
            logger.error("使用Kryo序列化时发生错误",e);
            throw new SerializeException("使用Kryo序列化时发生错误");
        }
    }

    @Override
    public Object deSerializer(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)){
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            Object o = kryo.readObject(input, clazz);
            KRYO_THREAD_LOCAL.remove();
            return o;

        }catch (Exception e){
            logger.error("使用Kryo反序列化时发生错误",e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }


    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
