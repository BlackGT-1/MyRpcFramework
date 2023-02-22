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
 * 说明 ：默认服务注册表，保存服务端本地服务
 */
public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();

    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service,String serviceName) {
        //如果该服务被注册过了，则返回
        if(registeredService.contains(serviceName)){
            return;
        }
        //如果没有被注册过，则添加服务名称
        registeredService.add(serviceName);
        serviceMap.put(serviceName,service);
        logger.info("往接口: {} 注册服务: {}",service.getClass().getInterfaces(),serviceName);
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
