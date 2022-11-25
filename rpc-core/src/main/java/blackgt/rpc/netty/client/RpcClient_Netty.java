package blackgt.rpc.netty.client;

import blackgt.rpc.codec.defaultDecoder;
import blackgt.rpc.codec.defaultEncoder;
import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.serializer.JsonSerializer;
import blackgt.rpc.serializer.KryoSerializer;
import blackgt.rpc.universalInterface.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author blackgt
 * @Date 2022/11/22 15:27
 * @Version 1.0
 * 说明 ：Netty远程调用客户端
 */
public class RpcClient_Netty implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient_Netty.class);
    //端口、地址
    private String host;
    private int port;

    private static final Bootstrap bootstrap;


    public RpcClient_Netty(String host,int port){
        this.host = host;
        this.port = port;
    }

    static {
        //线程组
        NioEventLoopGroup group = new NioEventLoopGroup();
        //客户端配置类
        bootstrap = new Bootstrap();
        //绑定配置
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //解码器，编码器，处理器
                        pipeline.addLast(new defaultDecoder())
                                .addLast(new defaultEncoder(new KryoSerializer()))
                                .addLast(new RpcClientResponseHandler_Netty());
                    }
                });
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("客户端正在连接到服务器,地址:{}端口号:{}",host,port);
            Channel channel = future.channel();
            if(channel!=null){
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            logger.error("请求发送过程中出现错误 {}",e);
        }
        return null;
    }
}
