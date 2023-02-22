package blackgt.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author blackgt
 * @Date 2022/11/19 15:06
 * @Version 1.0
 * 说明 ：
 */
@Getter
@AllArgsConstructor
public enum RpcErrorMessageEnums {
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),
    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_CAN_NOT_BE_FOUND("没有找到指定的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务没有实现任何接口"),
    REQUEST_NOT_MATCH_RESPONSE("返回结果错误！请求和返回的相应不匹配"),
    UNKNOWN_PROTOCOL("未知的协议"),
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),
    FAILED_TO_CONNECT_SERVICEREGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("服务注册失败"),
    SERIALIZER_NOT_FOUND("没有找到序列化器"),
    FAILED_TO_INVOKE_SERVICE("服务调用失败"),
    SERVICE_SCAN_PACKAGE_NOT_FOUND("启动类注解缺失"),
    UNKNOWN_ERROR("出现未知错误")
    ;

    private final String message;
}
