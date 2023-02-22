package blackgt.rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @Author blackgt
 * @Date 2022/12/18 11:10
 * @Version 1.0
 * 说明 ：
 */
public class RoundRobinLoadBalancer implements LoadBalancer{
    private int index = 0;
    @Override
    public Instance selectServiceAddress(List<Instance> instances) {
        if(index >= instances.size()){
            //取余
            index %= instances.size();
        }
        return instances.get(index++);

    }
}
