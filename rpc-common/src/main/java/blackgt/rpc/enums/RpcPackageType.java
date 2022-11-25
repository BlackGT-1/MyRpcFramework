package blackgt.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author blackgt
 * @Date 2022/11/21 22:16
 * @Version 1.0
 * 说明 ：
 */
@AllArgsConstructor
@Getter
public enum RpcPackageType {
    REQUEST_TYPE(0),
    RESPONSE_TYPE(1);
    private final int typeCode;
}
