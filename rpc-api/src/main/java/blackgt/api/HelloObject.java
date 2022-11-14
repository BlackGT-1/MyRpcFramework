package blackgt.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author blackgt
 * @Date 2022/11/13 10:50
 * @Version 1.0
 * 说明 ：
 */
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
