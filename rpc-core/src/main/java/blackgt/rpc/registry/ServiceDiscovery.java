package blackgt.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @Author blackgt
 * @Date 2022/12/16 21:00
 * @Version 1.0
 * 说明 ：
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名称查找服务实体
     * @param name 服务名称
     * @return 服务实体
     */
    InetSocketAddress findService(String name);
}
