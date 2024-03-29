package blackgt.rpc.transport.proxy;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.transport.RpcClient;
import blackgt.rpc.transport.netty.client.RpcClient_Netty;
import blackgt.rpc.transport.socket.client.SocketClient;
import blackgt.rpc.util.RpcValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Author blackgt
 * @Date 2022/11/28 22:40
 * @Version 1.0
 * 说明 ：
 */
public class RpcClientProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    private final RpcClient client;
    public RpcClientProxy(RpcClient client){
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }
    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        logger.info("调用方法:{}-{}",method.getDeclaringClass().getName(),method.getName());
        RpcRequest rpcRequest = new RpcRequest(
                UUID.randomUUID().toString(),
                method.getDeclaringClass().getName(),
                method.getName(),
                args,
                method.getParameterTypes(),
                false);
        RpcResponse response = null;
        if(client instanceof RpcClient_Netty){
            try {
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRpcRequest(rpcRequest);
                response = completableFuture.get();
            } catch (Exception e) {
                logger.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if(client instanceof SocketClient){
            response = (RpcResponse)client.sendRpcRequest(rpcRequest);
        }
        RpcValidator.Verify(rpcRequest,response);
        return response.getData();
    }
}
