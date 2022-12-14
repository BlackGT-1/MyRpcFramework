package blackgt.rpc.proxy;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.universalInterface.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

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
                method.getParameterTypes());
        return client.sendRpcRequest(rpcRequest);
    }
}
