package blackgt.rpc.registry;

/**
 * @Author blackgt
 * @Date 2022/11/18 16:24
 * @Version 1.0
 * 说明 :服务注册表的接口
 */
public interface ServiceRegistry {
    /**
     * 注册一个服务
     * @param service 服务
     * @param <T> 服务实体类
     */
    <T> void register(T service);

    /**
     * 根据服务名称获取服务实例
     * @param serviceName 服务名称
     * @return 服务实例
     */
    Object getService(String serviceName);

}
