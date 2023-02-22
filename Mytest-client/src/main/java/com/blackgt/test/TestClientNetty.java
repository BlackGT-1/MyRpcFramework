package com.blackgt.test;

import blackgt.api.HelloObject;
import blackgt.api.HelloService;
import blackgt.rpc.transport.netty.client.RpcClient_Netty;
import blackgt.rpc.transport.proxy.RpcClientProxy;
import blackgt.rpc.registry.NacosServiceRegistry;
import blackgt.rpc.serializer.KryoSerializer;

/**
 * @Author blackgt
 * @Date 2022/11/22 16:14
 * @Version 1.0
 * 说明 ：
 */
public class TestClientNetty {
    public static void main(String[] args) {
        RpcClient_Netty client = new RpcClient_Netty();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        HelloObject mes = new HelloObject(12, "发送一条消息");
        String res = proxy.hello(mes);
        System.out.println(res);
    }
}
