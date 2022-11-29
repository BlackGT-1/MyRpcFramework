package blackgt.rpc.netty.client;

import blackgt.rpc.codec.defaultDecoder;
import blackgt.rpc.codec.defaultEncoder;
import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.serializer.JsonSerializer;
import blackgt.rpc.serializer.KryoSerializer;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.universalInterface.RpcClient;
import blackgt.rpc.util.RpcValidator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author blackgt
 * @Date 2022/11/22 15:20
 * @Version 2.0
 * 说明 ：Netty实现远程调用----客户端
 */
public class RpcClient_Netty implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient_Netty.class);
    //注册中心
    private final ServiceRegistry serviceRegistry;
    //配置类
    private static final Bootstrap bootstrap;

    //需要使用的序列化器
    private defaultSerializer serializer;

    public RpcClient_Netty(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    static {
        //线程组
        EventLoopGroup group = new NioEventLoopGroup();
        //客户端配置类
        bootstrap = new Bootstrap();
        //绑定配置
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true);
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ChannelPipeline pipeline = ch.pipeline();
//                        //解码器，编码器，处理器
//                        pipeline.addLast(new defaultDecoder())
//                                .addLast(new defaultEncoder(new KryoSerializer()))
//                                .addLast(new RpcClientResponseHandler_Netty());
//                    }
//                });
    }
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //检查序列化器是否设置
        if(serializer == null){
            logger.error("序列化器未设置");
            throw new RpcException(RpcErrorMessageEnums.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> res = new AtomicReference<>(null);
        try{
            InetSocketAddress service = serviceRegistry.findService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.getChannel(service, serializer);
            if(channel.isActive()){
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                   if(future1.isSuccess()){
                        logger.info("客户端成功发送消息:{}",rpcRequest.toString());
                   }else{
                       logger.error("发送消息时发生错误",future1.cause());
                   }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                //校验请求与响应
                RpcValidator.Verify(rpcRequest,rpcResponse);
                res.set(rpcResponse.getData());

            }else {
                System.exit(0);
            }
        }catch (InterruptedException e){
            logger.error("发送消息时发生错误",e);
        }
        return res.get();
    }

    //设置序列化器
    @Override
    public void setSerializer(defaultSerializer serializer) {
        this.serializer = serializer;
    }
}
