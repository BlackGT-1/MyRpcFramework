package blackgt.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author blackgt
 * @Date 2022/11/13 14:07
 * @Version 1.0
 * 说明 ：消费者向提供者发送的请求对象
 */
@Data
//开启建造者模式
@Builder
public class RpcRequest implements Serializable {
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
}
