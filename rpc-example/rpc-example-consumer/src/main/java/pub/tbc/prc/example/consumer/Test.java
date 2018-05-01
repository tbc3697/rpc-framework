package pub.tbc.prc.example.consumer;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pub.tbc.rpc.example.api.service.HelloRpc;
import pub.tbc.rpc.ioc.RevokerFactoryBean;

import java.util.Spliterator;
import java.util.Spliterators;

/**
 * Created by tbc on 2018/5/2.
 */
@Configuration
public class Test {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("pub.tbc.prc.example.consumer");
        System.out.println("consumer started...");
        HelloRpc helloRpc = context.getBean("helloRpc", HelloRpc.class);
        System.out.println(helloRpc.hi());

    }


    @Bean("helloRpc")
    public RevokerFactoryBean getRevokerFactoryBean() {
        return RevokerFactoryBean.builder()
                .targetInterface(HelloRpc.class)
                .loadBalanceName("protostuff")
                .remoteAppKey("rpc-example-provider")
                .groupName("default-group")
                .timeout(3000)
                .build();
    }
}
