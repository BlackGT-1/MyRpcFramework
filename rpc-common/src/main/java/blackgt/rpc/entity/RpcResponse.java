package blackgt.rpc.entity;

import blackgt.rpc.enums.ResponseMessageEnums;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author blackgt
 * @Date 2022/11/13 14:08
 * @Version 2.0
 * 说明 ：服务提供者执行完成/错误后向消费者发送结果对象
 */
@Data
public class RpcResponse<T> implements Serializable {

    /**
     * 响应id
     */
    private String requestId;
    /**
     * 状态码信息
     */
    private Integer statusCode;
    /**
     * 错误信息
     */
    private String msg;
    /**
     *  响应成功返回的数据
     */
    private T data;
    /**
     * 第一个<T>声明T为泛型，和类名区别开来，而不是T.class。
     */

    //返回调用成功的对象
    public static <T> RpcResponse<T> success(T data,String requestId){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setStatusCode(ResponseMessageEnums.SUCCESS.getCode());
        rpcResponse.setData(data);
        return rpcResponse;

    }
    //返回调用失败对象
    public static <T> RpcResponse<T> fail(ResponseMessageEnums status,String requestId){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setStatusCode(status.getCode());
        rpcResponse.setMsg(status.getMes());
        return rpcResponse;
    }
}
