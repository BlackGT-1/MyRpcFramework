package blackgt.rpc.registry;

import blackgt.rpc.config.SystemConstants;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import blackgt.rpc.util.NacosUtil;
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
 * @Version 2.0
 * 说明 ：Nacos注册中心
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(InetSocketAddress address, String serviceName) {
        try {
            NacosUtil.registerService(serviceName,address);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcErrorMessageEnums.REGISTER_SERVICE_FAILED);
        }
    }
}
