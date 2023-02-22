package blackgt.rpc.handler;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.transport.socket.util.ObjectReader;
import blackgt.rpc.transport.socket.util.ObjectWriter;
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

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private defaultSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler,defaultSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        //()中确保了每个资源,在语句结束时关闭
        try(InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()){
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object result = requestHandler.handler(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        }catch (IOException e){
            logger.error("有调用或发送时发生异常"+e);
        }
    }
}
