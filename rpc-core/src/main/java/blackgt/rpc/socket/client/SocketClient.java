package blackgt.rpc.socket.client;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.enums.ResponseMessageEnums;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import blackgt.rpc.registry.NacosServiceRegistry;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.socket.util.ObjectReader;
import blackgt.rpc.socket.util.ObjectWriter;
import blackgt.rpc.universalInterface.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Author blackgt
 * @Date 2022/11/22 16:41
 * @Version 2.0
 * 说明 ：客户端，通过Socket方式调用远程方法
 */
public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final ServiceRegistry serviceRegistry;
    private defaultSerializer serializer;

    public SocketClient() {
        this.serviceRegistry = new NacosServiceRegistry();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        if(serializer == null){
            logger.error("序列化器未添加");
            throw new RpcException(RpcErrorMessageEnums.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress service = serviceRegistry.findService(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(service);
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//            objectOutputStream.writeObject(rpcRequest);
//            objectOutputStream.flush();
//            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            ObjectWriter.writeObject(outputStream,rpcRequest,serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) obj;

            if(rpcResponse == null) {
                logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcErrorMessageEnums.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseMessageEnums.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcErrorMessageEnums.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        } catch (IOException e) {
            logger.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败: ", e);
        }
    }

    @Override
    public void setSerializer(defaultSerializer serializer) {

    }
}
