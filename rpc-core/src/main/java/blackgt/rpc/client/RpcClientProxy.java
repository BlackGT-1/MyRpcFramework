package blackgt.rpc.client;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author blackgt
 * @Date 2022/11/13 15:02
 * @Version 1.0
 * 说明 ：客户端动态代理
 *      由于在客户端这一侧我们并没有接口的具体实现类，
 *      就没有办法直接生成实例对象。
 *      我们可以通过动态代理的方式生成实例，
 *      并且调用方法时生成需要的RpcRequest对象并且发送给服务端
 */
public class RpcClientProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    /**
     * 指出服务器地址
     */
    private String host;
    /**
     * 指出服务端口号
     */
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //获取传入类的代理
    public <T> T getProxy(Class<T> clazz){
        /**
         * newProxyInstance三个参数：
         * 1：类加载器
         * 2：实现了哪些接口---数组
         * 3：代理处理器（传入自己）
         */
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用了方法：{}###{}, ",method.getDeclaringClass().getName(),method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .methodParameters(args)
                .methodParameterType(method.getParameterTypes())
                .build();
        RpcClient rpcClient = new RpcClient();
        return rpcClient.sendRequest(rpcRequest,host,port);
    }

}
