package blackgt.rpc.transport.netty.client;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import blackgt.rpc.loadBalancer.LoadBalancer;
import blackgt.rpc.loadBalancer.RandomLoadBalancer;
import blackgt.rpc.registry.NacosServiceDiscovery;
import blackgt.rpc.registry.ServiceDiscovery;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.transport.RpcClient;
import blackgt.rpc.util.RpcValidator;
import blackgt.rpc.util.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author blackgt
 * @Date 2022/11/22 15:20
 * @Version 3.0
 * 说明 ：Netty实现远程调用----客户端
 */
public class RpcClient_Netty implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient_Netty.class);
    private static final EventLoopGroup group;
    //配置类
    private static final Bootstrap bootstrap;

    static {
        //线程组
        group = new NioEventLoopGroup();
        //客户端配置类
        bootstrap = new Bootstrap();
        //绑定配置
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    //服务发现
    private final ServiceDiscovery serviceDiscovery;

    //需要使用的序列化器
    private defaultSerializer serializer;

    //处理服务器未处理的请求
    private final UnprocessRequests unprocessRequests;


    public RpcClient_Netty(){
        this(defaultSerializer.KRYO_SERIALIZER,new RandomLoadBalancer());
    }
    public RpcClient_Netty(LoadBalancer loadBalancer){
        this(defaultSerializer.KRYO_SERIALIZER,loadBalancer);
    }

    public RpcClient_Netty(Integer serializer){
        //默认使用随机负载均衡
        this(serializer,new RandomLoadBalancer());
    }

    public RpcClient_Netty(Integer serializer, LoadBalancer loadBalancer){
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = defaultSerializer.getByCode(serializer);
        this.unprocessRequests = SingletonFactory.getInstance(UnprocessRequests.class);
    }


    @Override
    public CompletableFuture<RpcResponse> sendRpcRequest(RpcRequest rpcRequest) {
        //检查序列化器是否设置
        if(serializer == null){
            logger.error("序列化器未设置");
            throw new RpcException(RpcErrorMessageEnums.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> res = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.findService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.getChannel(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessRequests.put(rpcRequest.getRequestId(), res);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    res.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return res;
    }
}
