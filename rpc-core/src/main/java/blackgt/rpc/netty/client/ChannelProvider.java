package blackgt.rpc.netty.client;

import blackgt.rpc.codec.defaultDecoder;
import blackgt.rpc.codec.defaultEncoder;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import blackgt.rpc.serializer.defaultSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author blackgt
 * @Date 2022/11/28 16:00
 * @Version 1.0
 * 说明 ：获取Channel对象
 */
public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap =initializeBootstrap();

    //最大重复尝试次数
    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel = null;

    //初始化配置类
    private static Bootstrap initializeBootstrap(){
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //超时等待时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                //开启心跳检测
                .option(ChannelOption.SO_KEEPALIVE,true)
                //开启Nagle算法
                .option(ChannelOption.TCP_NODELAY,true);
        return bootstrap;

    }
    public static Channel getChannel(InetSocketAddress inetSocketAddress, defaultSerializer serializer){
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch){
                ChannelPipeline pipeline = ch.pipeline();
                        //添加编码器
                pipeline
                        .addLast(new defaultEncoder(serializer))
                        //添加解码器
                        .addLast(new defaultDecoder())
                        //处理器
                        .addLast(new RpcClientResponseHandler_Netty());
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            connect(bootstrap,inetSocketAddress,countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("获取channel过程中发生错误");
        }
        return channel;
    }

    //连接客户端
    public static void connect(Bootstrap bootstrap,InetSocketAddress inetSocketAddress,CountDownLatch countDownLatch){
        connect(bootstrap,inetSocketAddress,MAX_RETRY_COUNT,countDownLatch);
    }

    /**
     *
     * @param bootstrap 配置类
     * @param inetSocketAddress
     * @param retry 重试次数
     * @param countDownLatch
     */
    public static void connect(Bootstrap bootstrap,InetSocketAddress inetSocketAddress,int retry,CountDownLatch countDownLatch){
        bootstrap.connect(inetSocketAddress)
                .addListener((ChannelFutureListener)future->{
                    if(future.isSuccess()){
                        logger.info("客户端连接成功");
                        channel = future.channel();
                        countDownLatch.countDown();
                        return;
                    }
                    if(retry == 0){
                        logger.error("重试次数用完,客户端连接失败");
                        countDownLatch.countDown();
                        throw new RpcException(RpcErrorMessageEnums.CLIENT_CONNECT_SERVER_FAILURE);
                    }
                    //第几次重试
                    int order = (MAX_RETRY_COUNT - retry)+1;
                    //本次重试时间延时
                    int delay = 1 << order;
                    logger.error("连接失败,时间: {},正在尝试第{}次重连......",new Date(),order);
                    bootstrap.config().group().schedule(()->
                            connect(bootstrap, inetSocketAddress, retry, countDownLatch),delay, TimeUnit.SECONDS);
                });
    }

}
