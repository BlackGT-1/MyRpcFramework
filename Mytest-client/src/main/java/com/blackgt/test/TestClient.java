package com.blackgt.test;

import blackgt.api.HelloObject;
import blackgt.api.HelloService;
import blackgt.rpc.transport.netty.client.RpcClient_Netty;
import blackgt.rpc.transport.proxy.RpcClientProxy;
import blackgt.rpc.transport.socket.client.SocketClient;

/**
 * @Author blackgt
 * @Date 2022/11/13 18:36
 * @Version 1.0
 * 说明 ：
 */
public class TestClient {
    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        RpcClientProxy proxy = new RpcClientProxy(socketClient);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject mes = new HelloObject(5, "发送一条消息");
        String hello = helloService.hello(mes);
        System.out.println(hello);
    }
}
