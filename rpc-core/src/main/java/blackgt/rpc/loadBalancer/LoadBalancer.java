package blackgt.rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @Author blackgt
 * @Date 2022/12/17 10:30
 * @Version 1.0
 * 说明 ：
 */
public interface LoadBalancer {
    /**
     * 负载均衡
     * @param instances 所有服务信息列表
     * @return 负载均衡策略
     */
    Instance selectServiceAddress(List<Instance> instances);
}
