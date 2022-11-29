package blackgt.test;

import blackgt.api.HelloService;
import blackgt.rpc.netty.server.RpcServer_Netty;
import blackgt.rpc.serializer.KryoSerializer;

/**
 * @Author blackgt
 * @Date 2022/11/22 16:11
 * @Version 1.0
 * 说明 ：
 */
public class TestNettyServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        RpcServer_Netty server = new RpcServer_Netty("127.0.0.1", 9000);
        server.setSerializer(new KryoSerializer());
        server.publishService(helloService, HelloService.class);
    }
}
