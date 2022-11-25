package blackgt.test;

import blackgt.api.HelloObject;
import blackgt.rpc.netty.server.RpcServer_Netty;
import blackgt.rpc.registry.defaultServiceRegistry;

/**
 * @Author blackgt
 * @Date 2022/11/22 16:11
 * @Version 1.0
 * 说明 ：
 */
public class TestNettyServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        defaultServiceRegistry defaultServiceRegistry = new defaultServiceRegistry();
        //注册服务
        defaultServiceRegistry.register(helloService);
        RpcServer_Netty rpcServer_netty = new RpcServer_Netty();
        rpcServer_netty.startServer(8999);

    }
}
