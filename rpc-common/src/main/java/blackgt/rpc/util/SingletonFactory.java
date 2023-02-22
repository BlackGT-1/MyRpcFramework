package blackgt.rpc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author blackgt
 * @Date 2022/12/28 18:30
 * @Version 1.0
 * 说明 ：
 */
public class SingletonFactory {
    private static Map<Class,Object> objectMap = new HashMap<>();
    private SingletonFactory(){}

    public static <T> T getInstance(Class<T> tClass){
        Object instance = objectMap.get(tClass);
        synchronized (tClass) {
            if (instance == null) {
                try {
                    instance = tClass.newInstance();
                    objectMap.put(tClass, instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return tClass.cast(instance);
    }

}
