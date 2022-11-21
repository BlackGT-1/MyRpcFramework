package blackgt.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author blackgt
 * @Date 2022/11/13 14:33
 * @Version 1.0
 * 说明 ：方法被调用的响应信息
 */
@AllArgsConstructor
@Getter
public enum ResponseMessageEnums {
    SUCCESS(200,"方法调用成功"),
    Fail(500,"方法调用失败"),
    CANNOT_FOUND_METHOD(506,"没有找到该方法"),
    CANNOT_FOUND_CLASS(507,"没有找到该类");

    private final int code;
    private final String mes;

}
