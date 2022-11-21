package blackgt.rpc.registry;

import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author blackgt
 * @Date 2022/11/18 16:21
 * @Version 1.0
 * 说明 ：默认的自定义服务注册表
 */
public class defaultServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(defaultServiceRegistry.class);

    private final Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    @Override
    public synchronized  <T> void register(T service) {
        //获取服务的规范名字
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
            serviceMap.put(i.getCanonicalName(),service);
        }
        logger.info("往接口: {} 注册服务: {}",serviceInterfaces,serviceName);
    }

    //获取服务
    @Override
    public synchronized Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcException(RpcErrorMessageEnums.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
