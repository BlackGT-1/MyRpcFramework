package com.blackgt.test;

import blackgt.api.HelloObject;
import blackgt.api.HelloService;
import blackgt.rpc.client.RpcClientProxy;

/**
 * @Author blackgt
 * @Date 2022/11/13 18:36
 * @Version 1.0
 * 说明 ：
 */
public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(15, "我叼你妈的");
        String hello = proxy.hello(object);
        System.out.println(hello);

    }
}
