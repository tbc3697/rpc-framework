package pub.tbc.rpc.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by tbc on 2018/4/22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {

    String serviceName() default "";

    String version() default "1.0";

    /**
     * 序列化协议
     */
    String serializer() default "";




}
