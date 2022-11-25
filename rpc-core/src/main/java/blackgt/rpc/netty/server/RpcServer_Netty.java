package blackgt.rpc.netty.server;

import blackgt.rpc.codec.defaultDecoder;
import blackgt.rpc.codec.defaultEncoder;
import blackgt.rpc.serializer.JsonSerializer;
import blackgt.rpc.serializer.KryoSerializer;
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

/**
 * @Author blackgt
 * @Date 2022/11/21 16:31
 * @Version 1.0
 * 说明 ：
 */
public class RpcServer_Netty implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer_Netty.class);

    @Override
    public void startServer(int port) {
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
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //指定编码器，指定用Jackson进行序列化
                            pipeline.addLast(new defaultEncoder(new KryoSerializer()));
                            //解码器
                            pipeline.addLast(new defaultDecoder());
                            //处理器
                            pipeline.addLast(new RpcServerRequestHandler_Netty());
                        }
                    });
            //异步地绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

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
