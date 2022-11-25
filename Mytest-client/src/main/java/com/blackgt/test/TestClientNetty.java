package com.blackgt.test;

import blackgt.api.HelloObject;
import blackgt.api.HelloService;
import blackgt.rpc.client.RpcClientProxy;
import blackgt.rpc.netty.client.RpcClient_Netty;
import blackgt.rpc.universalInterface.RpcClient;

/**
 * @Author blackgt
 * @Date 2022/11/22 16:14
 * @Version 1.0
 * 说明 ：
 */
public class TestClientNetty {
    public static void main(String[] args) {
        RpcClient client = new RpcClient_Netty("127.0.0.1",8999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "");
        String res = proxy.hello(object);
        System.out.println(res);

    }
}
