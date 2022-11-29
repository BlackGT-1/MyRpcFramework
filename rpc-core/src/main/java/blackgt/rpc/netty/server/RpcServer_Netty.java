package blackgt.rpc.netty.server;

import blackgt.rpc.codec.defaultDecoder;
import blackgt.rpc.codec.defaultEncoder;
import blackgt.rpc.config.SystemConstants;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import blackgt.rpc.provider.ServiceProvider;
import blackgt.rpc.provider.ServiceProviderImpl;
import blackgt.rpc.registry.NacosServiceRegistry;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.serializer.JsonSerializer;
import blackgt.rpc.serializer.KryoSerializer;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.universalInterface.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * @Author blackgt
 * @Date 2022/11/29 20:00
 * @Version 2.0
 * 说明 ：
 */
public class RpcServer_Netty implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer_Netty.class);

    private final String host;
    private final int port;

    //注册中心
    private final ServiceRegistry serviceRegistry;
    //服务提供者
    private final ServiceProvider serviceProvider;
    //序列化器
    private defaultSerializer serializer;

    public RpcServer_Netty(String host,int port){
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
    }
    public RpcServer_Netty(String host,int port,ServiceRegistry serviceRegistry){
        this.host = host;
        this.port = port;
        this.serviceRegistry = serviceRegistry;
        serviceProvider = new ServiceProviderImpl();
    }




    @Override
    public void startServer() {
        //创建两个线程组（线程池）
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //LoggingHandler可以对入站\出站事件进行日志记录，从而方便我们进行问题排查。
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //将不能处理的客户端连接请求放在队列中等待处理,backlog参数指定了队列的大小
                    .option(ChannelOption.SO_BACKLOG,256)
                    //测试链接的状态
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    //开启Nagle算法，提高较慢的广域网传输效率
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //指定编码器，指定用Jackson进行序列化
                            pipeline.addLast(new defaultEncoder(serializer));
                            //解码器
                            pipeline.addLast(new defaultDecoder());
                            //处理器
                            pipeline.addLast(new RpcServerRequestHandler_Netty());
                        }
                    });
            //异步地绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(host,port).sync();

            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e){
            //捕获中断异常
            logger.error("服务端发生启动错误:{}",e);
        }finally {
            //优雅的关闭线程组（线程池）
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setSerializer(defaultSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null){
            logger.error("没有设置序列化器");
            throw new RpcException(RpcErrorMessageEnums.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(
                new InetSocketAddress(host,port),
                serviceClass.getCanonicalName());
        startServer();
    }
}
