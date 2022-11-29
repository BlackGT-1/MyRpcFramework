package blackgt.rpc.handler;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.enums.ResponseMessageEnums;
import blackgt.rpc.provider.ServiceProvider;
import blackgt.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author blackgt
 * @Date 2022/11/19 16:41
 * @Version 2.0
 * 说明 ：执行过程调用的处理器
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;
    static {
        serviceProvider = new ServiceProviderImpl();
    }
    public Object handler(RpcRequest rpcRequest){
        Object res = null;
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        try {
            res = InvokeTargetMethod(rpcRequest,service);
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }catch (IllegalAccessException | InvocationTargetException e){
            logger.error("调用或者发送时出现错误: {}",e);
        }
        return res;
    }
    //通过反射机制调用目标类的方法
    public Object InvokeTargetMethod(RpcRequest rpcRequest,Object service)throws IllegalAccessException,InvocationTargetException{
        Method method;
        try{
            method=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getMethodParameterType());
        }catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseMessageEnums.CANNOT_FOUND_METHOD,rpcRequest.getRequestId());
        }
        return method.invoke(service,rpcRequest.getMethodParameters());
    }
}
