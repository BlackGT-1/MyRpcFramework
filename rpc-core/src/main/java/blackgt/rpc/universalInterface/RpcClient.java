package blackgt.rpc.universalInterface;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.serializer.defaultSerializer;

/**
 * @Author blackgt
 * @Date 2022/11/21 15:38
 * @Version 2.0
 * 说明 ：客户端通用接口
 */
public interface RpcClient {
    /**
     * 客户端发送远程调用请求
     * @param rpcRequest 请求格式
     * @return 返回结果
     */
    Object sendRpcRequest(RpcRequest rpcRequest);

    /**
     * 设置(反)序列化器
     * @param serializer (反)序列化器
     */
    void setSerializer(defaultSerializer serializer);

}
