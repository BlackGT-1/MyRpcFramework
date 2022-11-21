package blackgt.rpc.client;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.enums.ResponseMessageEnums;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @Author blackgt
 * @Date 2022/11/13 15:02
 * @Version 2.0
 * 说明 ：
 */
public class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    public Object sendRequest(RpcRequest rpcRequest, String host, int port){
//        try (Socket socket = new Socket(host,port)){
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//            objectOutputStream.writeObject(rpcRequest);
//            objectOutputStream.flush();
//            return objectInputStream.readObject();
//        }catch (IOException | ClassNotFoundException e){
//            logger.error("调用时有错误发生"+e);
//            return null;
//        }
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            if(rpcResponse == null) {
                logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcErrorMessageEnums.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseMessageEnums.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcErrorMessageEnums.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败: ", e);
        }
    }
}
