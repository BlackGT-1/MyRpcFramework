package blackgt.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author blackgt
 * @Date 2022/11/21 22:27
 * @Version 1.0
 * 说明 ：字节流中标识序列化/反序列化的方式
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0),
    JACKSON(1);
    private final int code;
}
