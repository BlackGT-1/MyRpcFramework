package com.blackgt.test;

import blackgt.api.HelloObject;
import blackgt.api.HelloService;
import blackgt.rpc.netty.client.RpcClient_Netty;
import blackgt.rpc.proxy.RpcClientProxy;
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
        //创建Netty客户端指定注册中心
        RpcClient_Netty client = new RpcClient_Netty(new NacosServiceRegistry());
        //指定（反）序列化器
        client.setSerializer(new KryoSerializer());

        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloServiceProxy = rpcClientProxy.getProxy(HelloService.class);

        HelloObject obj = new HelloObject(111, "send a message");
        String hello = helloServiceProxy.hello(obj);
        System.out.println(hello);

    }
}
