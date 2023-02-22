package blackgt.rpc.registry;

import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import blackgt.rpc.loadBalancer.LoadBalancer;
import blackgt.rpc.loadBalancer.RandomLoadBalancer;
import blackgt.rpc.util.NacosUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author blackgt
 * @Date 2022/12/16 21:02
 * @Version 2.0
 * 说明 ：服务发现
 */
public class NacosServiceDiscovery implements ServiceDiscovery{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);
    private final LoadBalancer loadBalancer;

    /**
     * 初始化负载均衡策略，默认采用随机
     * @param loadBalancer 负载均衡策略
     */
    public NacosServiceDiscovery(LoadBalancer loadBalancer){
        if(loadBalancer == null){
            this.loadBalancer = new RandomLoadBalancer();
        }else {
            this.loadBalancer = loadBalancer;
        }
    }

    @Override
    public InetSocketAddress findService(String name) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(name);
            if(instances.size() == 0){
                logger.error("对不起，找不到对应的服务:"+name);
                throw new RpcException(RpcErrorMessageEnums.SERVICE_CAN_NOT_BE_FOUND);
            }
            Instance instance = loadBalancer.selectServiceAddress(instances);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        }catch (NacosException e){
            logger.error("从Nacos获取服务过程中发生错误");
        }
        return null;
    }
}
