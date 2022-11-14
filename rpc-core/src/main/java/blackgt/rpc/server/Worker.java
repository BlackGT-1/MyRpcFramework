package blackgt.rpc.server;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;
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
 * @Date 2022/11/13 16:07
 * @Version 1.0
 * 说明 ：
 */
public class Worker implements Runnable{

    private Socket socket;
    private Object service;
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    public Worker(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             RpcRequest rpcRequest  = (RpcRequest)objectInputStream.readObject();
             Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getMethodParameterType());
             Object returnObject = method.invoke(service, rpcRequest.getMethodParameters());
             objectOutputStream.writeObject(RpcResponse.success(returnObject));
             objectOutputStream.flush();
        }catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            logger.error("有调用或发送时发生异常"+e);
        }
    }
}
