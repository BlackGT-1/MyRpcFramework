package blackgt.rpc.registry;

import blackgt.rpc.config.SystemConstants;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author blackgt
 * @Date 2022/11/25 13:04
 * @Version 1.0
 * 说明 ：Nacos注册中心
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static final String serverAddress = SystemConstants.NACOS_SERVER_ADDRESS+":"+SystemConstants.NACOS_PORT;
    private static final NamingService namingService;

    static {
        try {
            //连接到Nacos注册中心
            namingService = NamingFactory.createNamingService(serverAddress);
        }catch (NacosException e){
            logger.error("连接Nacos时发生错误:{}",e);
            throw new RpcException(RpcErrorMessageEnums.FAILED_TO_CONNECT_SERVICEREGISTRY);
        }
    }

    @Override
    public void register(InetSocketAddress address, String serviceName) {
        try {
            //向Nacos注册一个服务实例
            namingService.registerInstance(serviceName,address.getHostName(),address.getPort());
        }catch (NacosException e){
            logger.error("Nacos注册过程中发生错误:{}",e);
            throw new RpcException(RpcErrorMessageEnums.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress findService(String serviceName) {
        try{
            //获得提供某个服务的所有提供者的列表
            List<Instance> allInstances = namingService.getAllInstances(serviceName);
            Instance instance = allInstances.get(0);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        }catch (NacosException e){
            logger.error("向Nacos获取服务时有错误发生 {}",e);
        }
        return null;
    }
}
