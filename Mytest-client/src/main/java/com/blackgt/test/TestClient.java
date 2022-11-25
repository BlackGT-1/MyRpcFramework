package com.blackgt.test;

import blackgt.api.HelloObject;
import blackgt.api.HelloService;
import blackgt.rpc.client.RpcClientProxy;
import blackgt.rpc.socket.client.SocketClient;

import java.util.Arrays;

/**
 * @Author blackgt
 * @Date 2022/11/13 18:36
 * @Version 1.0
 * 说明 ：
 */
public class TestClient {
    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient("127.0.0.1", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(socketClient);
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(15, "send a message");
        String hello = proxy.hello(object);
        System.out.println(hello);
    }
}
