package blackgt.rpc.entity;

import blackgt.rpc.enums.ResponseMessage;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author blackgt
 * @Date 2022/11/13 14:08
 * @Version 1.0
 * 说明 ：提供者执行完成/错误后向消费者发送结果对象
 */
@Data
public class RpcResponse<T> implements Serializable {
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

    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatusCode(ResponseMessage.SUCCESS.getCode());
        rpcResponse.setData(data);
        return rpcResponse;

    }
    public static <T> RpcResponse<T> fail(ResponseMessage status){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatusCode(status.getCode());
        rpcResponse.setMsg(status.getMes());
        return rpcResponse;
    }
}
