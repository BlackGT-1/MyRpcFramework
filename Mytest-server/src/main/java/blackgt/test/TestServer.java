package blackgt.test;

import blackgt.api.HelloService;

/**
 * @Author blackgt
 * @Date 2022/11/13 16:52
 * @Version 1.0
 * 说明 ：
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
    }
}
