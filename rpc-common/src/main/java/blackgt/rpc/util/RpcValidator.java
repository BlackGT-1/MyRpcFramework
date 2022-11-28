package blackgt.rpc.util;

import blackgt.rpc.entity.RpcRequest;
import blackgt.rpc.entity.RpcResponse;
import blackgt.rpc.enums.ResponseMessageEnums;
import blackgt.rpc.enums.RpcErrorMessageEnums;
import blackgt.rpc.exceptions.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @Author blackgt
 * @Date 2022/11/27 21:00
 * @Version 1.0
 * 说明 ：校验请求与响应
 */
public class RpcValidator {
    private static final Logger logger = LoggerFactory.getLogger(RpcValidator.class);

    private RpcValidator(){

    }

    public static void Verify(RpcRequest rpcRequest, RpcResponse rpcResponse){
        if(rpcResponse == null){
            logger.error("调用服务失败,调用服务名:{}",rpcRequest.getInterfaceName());
            throw new RpcException(RpcErrorMessageEnums.FAILED_TO_INVOKE_SERVICE);
        }
        //检查请求和响应的id是否相同
        if(!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())){
            logger.error("请求Id与响应Id不同");
            throw new RpcException(RpcErrorMessageEnums.REQUEST_NOT_MATCH_RESPONSE);
        }
        //检查响应状态码信息是否为空或是返回获取失败的状态码
        if(rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseMessageEnums.SUCCESS)){
            logger.error("服务状态码信息异常,调用服务名:{},RpcResponse:{}",rpcRequest.getInterfaceName(),rpcResponse.toString());
            throw new RpcException(RpcErrorMessageEnums.FAILED_TO_INVOKE_SERVICE);
        }
    }
}
