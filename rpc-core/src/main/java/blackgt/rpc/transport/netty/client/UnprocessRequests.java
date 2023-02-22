package blackgt.rpc.transport.netty.client;

import blackgt.rpc.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author blackgt
 * @Date 2022/12/29 17:00
 * @Version 1.0
 * 说明 ：服务器未处理的请求
 */
public class UnprocessRequests {
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId,CompletableFuture<RpcResponse> future){
        unprocessedResponseFutures.put(requestId,future);
    }

    public void remove(String requestId){
        unprocessedResponseFutures.remove(requestId);
    }
    public void complete(RpcResponse response){
        CompletableFuture<RpcResponse> responseCompletableFuture = unprocessedResponseFutures.remove(response.getRequestId());
        if(responseCompletableFuture != null){
            responseCompletableFuture.complete(response);
        }else {
            throw new IllegalStateException();
        }
    }

}
