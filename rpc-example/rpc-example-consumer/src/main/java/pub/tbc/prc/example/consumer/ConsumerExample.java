package pub.tbc.prc.example.consumer;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pub.tbc.rpc.example.api.service.HelloRpc;
import pub.tbc.rpc.example.api.service.LoggerManager;
import pub.tbc.rpc.ioc.RevokerFactoryBean;
import pub.tbc.toolkit.core.Sleeps;
import pub.tbc.toolkit.core.collect.MapBuilder;

/**
 * Created by tbc on 2018/5/2.
 */
@Configuration
public class ConsumerExample {
    public static void main(String[] args) {
        LoggerManager.logSetting();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("pub.tbc.prc.example.consumer");
        System.out.println("consumer started...");
        HelloRpc helloRpc = context.getBean("helloRpc", HelloRpc.class);
        System.out.println(helloRpc.hi("HelloWorld"));
        for (int i = 0; i < 1000; i++) {
            System.out.println(helloRpc.hi());
            System.out.println(helloRpc.hi("ha-" + i));
            System.out.println(helloRpc.hi(new MapBuilder<String, String>().put("k" + i, "v" + i).build()));
            Sleeps.seconds(5);
        }

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
