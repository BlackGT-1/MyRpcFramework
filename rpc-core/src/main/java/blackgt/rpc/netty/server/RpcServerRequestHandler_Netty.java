package blackgt.rpc.netty.server;

import blackgt.rpc.config.SystemConstants;
import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.handler.RequestHandler;
import blackgt.rpc.util.ThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @Author blackgt
 * @Date 2022/11/22 14:56
 * @Version 1.0
 * 说明 ：处理Rpc请求的Handler
 */
public class RpcServerRequestHandler_Netty extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerRequestHandler_Netty.class);
    private static RequestHandler requestHandler;

    private static final ExecutorService threadPool;

    static {
        requestHandler = new RequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(SystemConstants.THREAD_ID_PREFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        threadPool.execute(()->{
            try {
                logger.info("服务器接收到请求:{}",msg);
                Object res = requestHandler.handler(msg);
                ChannelFuture channelFuture = ctx.writeAndFlush(RpcResponse.success(res, msg.getRequestId()));
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }finally {
                ReferenceCountUtil.release(msg);
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("服务调用过程发生错误");
        cause.printStackTrace();
        ctx.close();
    }
}
