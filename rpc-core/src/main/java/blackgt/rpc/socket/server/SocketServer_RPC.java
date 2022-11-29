package blackgt.rpc.socket.server;

import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import blackgt.rpc.handler.RequestHandler;
import blackgt.rpc.handler.RequestHandlerThread;
import blackgt.rpc.provider.ServiceProvider;
import blackgt.rpc.provider.ServiceProviderImpl;
import blackgt.rpc.registry.NacosServiceRegistry;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.universalInterface.RpcServer;
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
 * @Version 1.0
 * 说明 ：
 */
public class SocketServer_RPC implements RpcServer {
    public static final Logger logger = LoggerFactory.getLogger(SocketServer_RPC.class);

    private final ExecutorService threadPool;
    private final String host;
    private final int port;
    private defaultSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();

    public final ServiceRegistry serviceRegistry;
    public final ServiceProvider serviceProvider;

    public SocketServer_RPC(String host,int port){
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-server");
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
    }
    
    @Override
    public void startServer() {

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("正在开启服务器。。。");
            Socket socket;
            while((socket = serverSocket.accept())!=null){
                logger.info("有一个消费者接入{}:{}",socket.getInetAddress(),socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket,serviceRegistry,requestHandler,serializer));
            }
            threadPool.shutdown();
        }catch (IOException e){
            logger.error("服务器启动时发生错误:{}",e);
        }

    }

    @Override
    public void setSerializer(defaultSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null){
            logger.error("请设置序列化器");
            throw new RpcException(RpcErrorMessageEnums.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(new InetSocketAddress(host,port), serviceClass.getCanonicalName());
        startServer();
    }
}
