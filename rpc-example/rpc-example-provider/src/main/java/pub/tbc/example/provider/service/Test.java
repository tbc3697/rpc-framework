package pub.tbc.example.provider.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pub.tbc.rpc.example.api.service.HelloRpc;
import pub.tbc.rpc.ioc.ProviderFactoryBean;

/**
 * Created by tbc on 2018/5/2.
 */
@Slf4j
@Configuration
public class Test {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("pub.tbc.example.provider.service");
        System.out.println("provider started...");

    }

    @Bean("helloRpc")
    public ProviderFactoryBean getHelloRpc() {
        log.info("初始化 ProviderFactoryBean");
        return ProviderFactoryBean.builder()
                .appKey("rpc-example-provider")
                .serviceItf(HelloRpc.class)
                .serviceObject(new HelloRpcImpl())
                .weight(2)
                .groupName("default-group")
                .workerThreads(11)
                .serverPort("8081")
                .timeout(3000)
                .build();
    }
}
