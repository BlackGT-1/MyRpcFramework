package blackgt.rpc.netty.client;

import blackgt.rpc.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author blackgt
 * @Date 2022/11/22 15:38
 * @Version 1.0
 * 说明 ：
 */
public class RpcClientResponseHandler_Netty extends SimpleChannelInboundHandler<RpcResponse>{

    private static final Logger logger = LoggerFactory.getLogger(RpcClientResponseHandler_Netty.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            logger.info(String.format("客户端接收到消息：%s",msg));
            AttributeKey<RpcResponse> rpcResponseKey = AttributeKey.valueOf("rpcResponse"+msg.getRequestId());
            ctx.channel().attr(rpcResponseKey).set(msg);
            ctx.channel().close();
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
}
