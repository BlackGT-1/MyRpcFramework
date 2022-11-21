package blackgt.rpc.exceptions;

import blackgt.rpc.enums.RpcErrorMessageEnums;

/**
 * @Author blackgt
 * @Date 2022/11/19 15:01
 * @Version 1.0
 * 说明 ：远程调用异常
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcErrorMessageEnums error,String detail){
        super(error.getMessage()+":"+ detail);
    }
    public RpcException(String message,Throwable cause){
        super(message,cause);
    }
    public RpcException(RpcErrorMessageEnums error){
        super(error.getMessage());
    }
}
