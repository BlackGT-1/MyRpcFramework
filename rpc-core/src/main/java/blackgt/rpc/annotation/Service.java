package blackgt.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author blackgt
 * @Date 2022/12/30 23:30
 * @Version 1.0
 * 说明 ：表示一个服务提供类，用于远程接口的实现类
 * 放在一个类上，标识这个类提供一个服务
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    public String name() default "";
}
