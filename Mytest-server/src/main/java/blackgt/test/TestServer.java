package blackgt.test;

import blackgt.rpc.server.RpcServer;

/**
 * @Author blackgt
 * @Date 2022/11/13 16:52
 * @Version 1.0
 * 说明 ：
 */
public class TestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService,9000);
    }
}
