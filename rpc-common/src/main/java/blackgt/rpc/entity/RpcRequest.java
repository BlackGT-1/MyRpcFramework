package blackgt.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author blackgt
 * @Date 2022/11/13 14:07
 * @Version 2.0
 * 说明 ：消费者向提供者发送的请求对象
 */
@Data
@AllArgsConstructor
public class RpcRequest implements Serializable {
    public RpcRequest(){

    }

    /**
     * 请求id
     */
    private String requestId;
    /**
     * 待调用接口的名称
     */
    private String interfaceName;

    /**
     * 待调用方法的名称
     */
    private String methodName;

    /**
     * 待调用方法参数
     */
    private Object[] methodParameters;
    /**
     * 待调用方法的参数类型
     */
    private Class<?>[] methodParameterType;
    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;
}
