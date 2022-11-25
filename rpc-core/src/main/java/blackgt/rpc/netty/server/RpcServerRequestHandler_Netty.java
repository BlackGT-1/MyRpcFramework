package blackgt.rpc.netty.server;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.handler.RequestHandler;
import blackgt.rpc.registry.ServiceRegistry;
import blackgt.rpc.registry.defaultServiceRegistry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author blackgt
 * @Date 2022/11/22 14:56
 * @Version 1.0
 * 说明 ：处理Rpc请求的Handler
 */
public class RpcServerRequestHandler_Netty extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerRequestHandler_Netty.class);
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new defaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try {
            logger.info("服务器接收到请求：{}",msg);
            //获取请求中待调用的接口
            String interfaceName = msg.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            //调用服务
            Object res = requestHandler.handler(msg, service);
            //将发送成功的消息推送出
            ChannelFuture channelFuture = ctx.writeAndFlush(RpcResponse.success(res));
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("服务调用过程发生错误");
        cause.printStackTrace();
        ctx.close();
    }
}
