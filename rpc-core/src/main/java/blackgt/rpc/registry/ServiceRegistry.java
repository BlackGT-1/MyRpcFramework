package blackgt.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @Author blackgt
 * @Date 2022/11/25 15:54
 * @Version 2.0
 * 说明 ：注册中心通用接口
 */
public interface ServiceRegistry {
    /**
     * 添加一个服务进注册表
     * @param address 提供服务的地址
     * @param serviceName 服务名称
     */
    void register(InetSocketAddress address,String serviceName);

    /**
     * 查找服务
     * @param serviceName 服务名称
     * @return 服务实例
     */
    InetSocketAddress findService(String serviceName);
}
