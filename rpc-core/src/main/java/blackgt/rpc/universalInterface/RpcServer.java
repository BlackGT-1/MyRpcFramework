package blackgt.rpc.universalInterface;

import blackgt.rpc.serializer.defaultSerializer;

/**
 * @Author blackgt
 * @Date 2022/11/21 15:37
 * @Version 2.0
 * 说明 ：服务端通用接口
 */
public interface RpcServer {
    /**
     * 启动服务器
     */
    void startServer();

    void setSerializer(defaultSerializer serializer);

    <T> void publishService(Object service,Class<T> serviceClass);

}
