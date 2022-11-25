package blackgt.rpc.exceptions;

/**
 * @Author blackgt
 * @Date 2022/11/24 20:08
 * @Version 1.0
 * 说明 ：
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String msg){
        super(msg);
    }
}
