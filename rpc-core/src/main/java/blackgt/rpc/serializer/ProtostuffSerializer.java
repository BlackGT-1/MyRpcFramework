package blackgt.rpc.serializer;

import blackgt.rpc.enums.SerializerCode;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author blackgt
 * @Date 2022/12/9 13:00
 * @Version 1.0
 * 说明 ：ProtoBuf序列化器
 */
public class ProtostuffSerializer implements defaultSerializer{

    /*
     * 定义一个字节组缓冲区，避免每次序列化都重新申请Buffer
     */
    private LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    /**
     * 缓存Schema
     */
    private Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @Override
    public byte[] serializer(Object res) {
        Class aClass = res.getClass();
        Schema schema = getSchema(aClass);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(res,schema,buffer);
        }finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    public Object deSerializer(byte[] bytes, Class<?> clazz) {
        /**
         * 首先根据序列化对象获取其组织结构Schema
         */
        Schema schema = getSchema(clazz);
        Object obj = schema.newMessage();
        /**
         * 根据byte直接mergeFrom成一个对象。
         */
        ProtostuffIOUtil.mergeFrom(bytes,obj,schema);
        return obj;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("PROTOSTUFF").getCode();
    }

    /**
     * 获取序列化对象的组织结构
     */
    private Schema getSchema(Class clazz){
        Schema schema = schemaCache.get(clazz);
        if(schema == null){
            //schema通过RuntimeSchema进行懒创建并缓存
            schema = RuntimeSchema.getSchema(clazz);
            if(schema !=null){
                schemaCache.put(clazz,schema);
            }
        }
        return schema;
    }
}
