package pub.tbc.rpc.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by tbc on 2018/4/22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcInject {

    String version() default "1.0";

    String serviceName() default "";

    int retries() default 0;

    long timeout() default 10000;
}
