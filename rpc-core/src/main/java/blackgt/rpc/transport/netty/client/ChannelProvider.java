package blackgt.rpc.transport.netty.client;

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
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

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

    private static Map<String,Channel> channelMap = new ConcurrentHashMap<>();


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
    public static Channel getChannel(InetSocketAddress inetSocketAddress, defaultSerializer serializer) throws InterruptedException{
        String key = inetSocketAddress.toString()+serializer.getCode();
        if(channelMap.containsKey(key)){
            Channel channel = channelMap.get(key);
            if(channelMap!=null && channel.isActive()){
                return channel;
            }else {
                channelMap.remove(key);
            }
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch){
                ChannelPipeline pipeline = ch.pipeline();
                        //添加编码器
                pipeline
                        .addLast(new defaultEncoder(serializer))
                        .addLast(new IdleStateHandler(0,5,0,TimeUnit.SECONDS))
                        //添加解码器
                        .addLast(new defaultDecoder())
                        //处理器
                        .addLast(new RpcClientResponseHandler_Netty());
            }
        });
        Channel channel = null;
        try {
            channel = connect(bootstrap,inetSocketAddress);
        }catch (ExecutionException e){
            logger.error("客户端连接时有错误发生",e);
            return null;
        }
        channelMap.put(key,channel);
        return channel;
    }



    /**
     *
     * @param bootstrap 配置类
     * @param inetSocketAddress 服务地址
     */
    private static Channel connect(Bootstrap bootstrap,InetSocketAddress inetSocketAddress)throws ExecutionException,InterruptedException{
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener)future->{
           if(future.isSuccess()){
               logger.info("客户端连接成功");
               completableFuture.complete(future.channel());
           }else {
               throw new IllegalStateException();
           }
        });
        return completableFuture.get();
    }

}
