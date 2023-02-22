package blackgt.rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @Author blackgt
 * @Date 2022/12/18 11:00
 * @Version 1.0
 * 说明 ：随机实现负载均衡
 */
public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public Instance selectServiceAddress(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
