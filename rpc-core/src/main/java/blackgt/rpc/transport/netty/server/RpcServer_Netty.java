package blackgt.rpc.transport.netty.server;

import blackgt.rpc.codec.defaultDecoder;
import blackgt.rpc.codec.defaultEncoder;
import blackgt.rpc.config.customShutdownHook;
import blackgt.rpc.provider.ServiceProviderImpl;
import blackgt.rpc.registry.NacosServiceRegistry;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.transport.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Author blackgt
 * @Date 2022/11/29 20:00
 * @Version 2.0
 * 说明 ：
 */
public class RpcServer_Netty extends AbstractRpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer_Netty.class);
    //序列化器
    private defaultSerializer serializer;

    public RpcServer_Netty(String host,int port){
        this(host,port,defaultSerializer.KRYO_SERIALIZER);
    }

    public RpcServer_Netty(String host,int port,Integer serializer){
        this.host = host;
        this.port = port;
        this.serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = defaultSerializer.getByCode(serializer);
        scanServices();
    }

    @Override
    public void startServer() {
        customShutdownHook.getShutdownHook().addClearAllHook();
        //创建两个线程组（线程池）
        EventLoopGroup bossGroup = new NioEventLoopGroup();
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

                            pipeline.addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS));
                            //指定编码器
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

}
