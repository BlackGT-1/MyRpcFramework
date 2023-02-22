package blackgt.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author blackgt
 * @Date 2022/12/30 23:33
 * @Version 1.0
 * 说明 ：服务扫描的基包
 *  放在启动的入口类上（main 方法所在的类），标识服务的扫描的包的范围
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceScan {

    public String value() default "";

}
