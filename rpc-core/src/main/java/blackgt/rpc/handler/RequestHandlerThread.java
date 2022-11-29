package blackgt.rpc.handler;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.universalInterface.RpcServer;
import com.esotericsoftware.kryo.DefaultSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * @Author blackgt
 * @Date 2022/11/19 16:51
 * @Version 1.0
 * 说明 ：
 */
public class RequestHandlerThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private Socket socket;
    private ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler;
    private defaultSerializer serializer;

    public RequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry, RequestHandler requestHandler,defaultSerializer serializer) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        //()中确保了每个资源,在语句结束时关闭
        try(InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()){
            //todo
        }catch (IOException e){
            logger.error("有调用或发送时发生异常"+e);
        }
    }
}
