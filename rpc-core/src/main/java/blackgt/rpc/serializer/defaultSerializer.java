package blackgt.rpc.serializer;

/**
 * @Author blackgt
 * @Date 2022/11/21 20:36
 * @Version 2.0
 * 说明 ：序列化/反序列化接口
 */
public interface defaultSerializer {
    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer PROTOSTUFF_SERIALIZER = 2;
    /**
     * 将目标对象序列化
     * @param res 目标对象
     * @return 字节数组
     */
    byte[] serializer(Object res);

    /**
     * 将目标字节数组反序列化
     * @param bytes 字节数组
     * @param clazz 序列化成的类
     * @return 结果对象
     */
    Object deSerializer(byte[] bytes,Class<?> clazz);

    int getCode();

    static defaultSerializer getByCode(int code){
        switch (code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new ProtostuffSerializer();
            default:
                return null;
        }
    }


}
