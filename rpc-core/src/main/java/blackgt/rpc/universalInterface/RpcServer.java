package blackgt.rpc.universalInterface;

/**
 * @Author blackgt
 * @Date 2022/11/21 15:37
 * @Version 1.0
 * 说明 ：
 */
public interface RpcServer {
    /**
     * 启动服务器
     * @param port 端口号
     */
    void startServer(int port);
}
