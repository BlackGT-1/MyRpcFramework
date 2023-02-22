package blackgt.test;

import blackgt.api.HelloService;
import blackgt.rpc.annotation.ServiceScan;
import blackgt.rpc.transport.netty.server.RpcServer_Netty;
import blackgt.rpc.serializer.KryoSerializer;

/**
 * @Author blackgt
 * @Date 2022/11/22 16:11
 * @Version 1.0
 * 说明 ：
 */
@ServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer_Netty server = new RpcServer_Netty("127.0.0.1", 8082);
        server.startServer();

    }
}
