package blackgt.rpc.transport.socket.server;

import blackgt.rpc.config.customShutdownHook;
import blackgt.rpc.handler.RequestHandler;
import blackgt.rpc.handler.RequestHandlerThread;
import blackgt.rpc.provider.ServiceProviderImpl;
import blackgt.rpc.registry.NacosServiceRegistry;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.transport.AbstractRpcServer;
import blackgt.rpc.util.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @Author blackgt
 * @Date 2022/11/27 15:03
 * @Version 2.0
 * 说明 ：
 */
public class SocketServer extends AbstractRpcServer {
    public static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;
    private defaultSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();

    public SocketServer(String host, int port) {
        this(host, port, defaultSerializer.KRYO_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = defaultSerializer.getByCode(serializer);
        scanServices();
    }
    
    @Override
    public void startServer() {

        try(ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host,port));
            logger.info("正在开启服务器。。。");
            //钩子函数
            customShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while((socket = serverSocket.accept())!=null){
                logger.info("有一个消费者接入{}:{}",socket.getInetAddress(),socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket,requestHandler,serializer));
            }
            threadPool.shutdown();
        }catch (IOException e){
            logger.error("服务器启动时发生错误:{}",e);
        }

    }
}
