package blackgt.test;

import blackgt.rpc.annotation.ServiceScan;
import blackgt.rpc.transport.socket.server.SocketServer;

/**
 * @Author blackgt
 * @Date 2022/11/13 16:52
 * @Version 1.0
 * 说明 ：
 */
@ServiceScan
public class TestServer {
    public static void main(String[] args) {
        SocketServer server = new SocketServer("127.0.0.1", 9999);
        server.startServer();
    }
}
