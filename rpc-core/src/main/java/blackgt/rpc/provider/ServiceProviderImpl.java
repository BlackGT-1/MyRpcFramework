package blackgt.rpc.provider;

import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author blackgt
 * @Date 2022/11/25 12:26
 * @Version 1.0
 * 说明 ：
 */
public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();

    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized  <T> void addServiceProvider(T service) {
        //获取服务的规范名字（全包名）
        String serviceName = service.getClass().getCanonicalName();
        //如果该服务被注册过了，则返回
        if(registeredService.contains(serviceName)){
            return;
        }
        //如果没有被注册过，则添加服务名称
        registeredService.add(serviceName);
        //获取服务的接口数组
        Class<?>[] serviceInterfaces = service.getClass().getInterfaces();
        if(serviceInterfaces.length==0){
            //抛出“注册的服务没有实现任何接口”异常
            throw new RpcException(RpcErrorMessageEnums.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> i : serviceInterfaces){
            //添加服务
            serviceMap.put(i.getCanonicalName(),service);
        }
        logger.info("往接口: {} 注册服务: {}",serviceInterfaces,serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        //获取服务
        Object service = serviceMap.get(serviceName);
        if(service == null){
            //抛出服务未发现异常
            throw new RpcException(RpcErrorMessageEnums.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
