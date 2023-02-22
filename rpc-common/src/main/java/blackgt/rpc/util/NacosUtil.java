package blackgt.rpc.util;

import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @Author blackgt
 * @Date 2022/12/17 11:00
 * @Version 1.0
 * 说明 ：
 */
public class NacosUtil {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    private static final String SERVER_ADDRESS = "127.0.0.1:8848";

    static {
        namingService = getNamingService();
    }

    public static NamingService getNamingService(){
        try {
            return NamingFactory.createNamingService(SERVER_ADDRESS);
        }catch (NacosException e){
            logger.error("连接到Nacos时发生错误:",e);
            throw new RpcException(RpcErrorMessageEnums.FAILED_TO_CONNECT_SERVICEREGISTRY);
        }
    }

    public static void registerService(String serviceName,InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName,address.getHostName(),address.getPort());
        NacosUtil.address = address;
        serviceNames.add(serviceName);
    }
    public static List<Instance> getAllInstance(String serviceName) throws NacosException{
        return namingService.getAllInstances(serviceName);
    }
    public static void loginOutAllRegistry(){
        if(!serviceNames.isEmpty() && address!=null){
            String hostName = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()){
                String next = iterator.next();
                try {
                    namingService.deregisterInstance(next,hostName,port);
                }catch (NacosException e){
                    logger.error("服务{}注销失败",next,e);
                }
            }
        }
    }

}
