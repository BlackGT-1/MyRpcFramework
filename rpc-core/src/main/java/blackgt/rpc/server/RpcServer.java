package blackgt.rpc.server;

import blackgt.rpc.handler.RequestHandler;
import blackgt.rpc.handler.RequestHandlerThread;
import blackgt.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @Author blackgt
 * @Date 2022/11/13 16:07
 * @Version 1.0
 * 说明 ：提供者代码
 */
public class RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    //服务注册表
    private final ServiceRegistry serviceRegistry;

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 20;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;


    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        ArrayBlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE_TIME,TimeUnit.SECONDS,workingQueue,threadFactory);
    }
    public void startServer(int port){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器正在启动。。。。。");
            Socket socket;
            while ((socket=serverSocket.accept()) !=null){
                logger.info("客户端连接成功,ip地址为:{} 端口号:{}",socket.getInetAddress(),socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket,serviceRegistry, requestHandler));
            }
            threadPool.shutdown();
        }catch (IOException e){
            logger.error("连接时发生错误"+e);
        }
    }
}
