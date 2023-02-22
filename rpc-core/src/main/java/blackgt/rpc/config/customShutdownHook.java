package blackgt.rpc.config;

import blackgt.rpc.util.NacosUtil;
import blackgt.rpc.util.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author blackgt
 * @Date 2022/12/17 16:30
 * @Version 1.0
 * 说明 ：关闭系统时调用钩子
 */
public class customShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(customShutdownHook.class);

    private static customShutdownHook shutdownHook = new customShutdownHook();
    public static customShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearAllHook(){
        logger.info("关闭系统将注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            NacosUtil.loginOutAllRegistry();
            ThreadPoolFactory.shutDownAllThreadPool();
        }));
    }
}
