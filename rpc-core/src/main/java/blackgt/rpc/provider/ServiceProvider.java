package blackgt.rpc.provider;

/**
 * @Author blackgt
 * @Date 2022/11/25 12:19
 * @Version 1.0
 * 说明 ：保存/提供服务实例对象接口
 */
public interface ServiceProvider {
    /**
     * 添加服务
     * @param service 待添加服务对象
     * @param serviceName 服务名称
     */
    <T> void addServiceProvider(T service,String serviceName);

    /**
     * 获取服务
     * @param serviceName 待获取服务名称
     * @return 待获取服务对象
     */
    Object getServiceProvider(String serviceName);
}
