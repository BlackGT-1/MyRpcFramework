package blackgt.test;

import blackgt.api.HelloService;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.registry.defaultServiceRegistry;
import blackgt.rpc.server.RpcServer;

/**
 * @Author blackgt
 * @Date 2022/11/13 16:52
 * @Version 1.0
 * 说明 ：
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new defaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.startServer(9000);
    }
}
