package blackgt.rpc.transport.netty.client;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.serializer.defaultSerializer;
import blackgt.rpc.util.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @Author blackgt
 * @Date 2022/11/22 15:38
 * @Version 3.0
 * 说明 ：
 */
public class RpcClientResponseHandler_Netty extends SimpleChannelInboundHandler<RpcResponse>{

    private static final Logger logger = LoggerFactory.getLogger(RpcClientResponseHandler_Netty.class);
    private final UnprocessRequests unprocessRequests;
    public RpcClientResponseHandler_Netty(){
        this.unprocessRequests = SingletonFactory.getInstance(UnprocessRequests.class);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            logger.info(String.format("客户端接收到消息：%s",msg));
            unprocessRequests.complete(msg);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("客户端调用过程中有错误发生");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                logger.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.getChannel((InetSocketAddress) ctx.channel().remoteAddress(), defaultSerializer.getByCode(defaultSerializer.KRYO_SERIALIZER));
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setHeartBeat(true);
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
