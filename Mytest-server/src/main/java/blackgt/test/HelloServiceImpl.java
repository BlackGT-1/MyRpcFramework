package blackgt.test;

import blackgt.api.HelloObject;
import blackgt.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author blackgt
 * @Date 2022/11/13 10:58
 * @Version 1.0
 * 说明 ：
 */
public class HelloServiceImpl implements HelloService {
    private static final Logger Logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        Logger.info("接收到：{}", object.getMessage());
        return "返回值id="+object.getId();
    }
}
