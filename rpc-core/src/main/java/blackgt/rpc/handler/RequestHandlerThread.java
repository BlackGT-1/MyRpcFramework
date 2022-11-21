package blackgt.rpc.handler;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.server.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public RequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry, RequestHandler requestHandler) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {
            RpcRequest rpcRequest  = (RpcRequest)objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object res = requestHandler.handler(rpcRequest, service);

            objectOutputStream.writeObject(RpcResponse.success(res));
            objectOutputStream.flush();
        }catch (IOException | ClassNotFoundException e){
            logger.error("有调用或发送时发生异常"+e);
        }
    }
}
