package pub.tbc.example.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pub.tbc.example.provider.service.HaServiceImpl;
import pub.tbc.example.provider.service.HelloRpcImpl;
import pub.tbc.example.provider.service.SecondServiceImpl;
import pub.tbc.rpc.example.api.service.HaService;
import pub.tbc.rpc.example.api.service.HelloRpc;
import pub.tbc.rpc.example.api.LoggerManager;
import pub.tbc.rpc.example.api.service.SecondService;
import pub.tbc.rpc.ioc.ProviderFactoryBean;

/**
 * Created by tbc on 2018/5/2.
 */
@Slf4j
@Configuration
public class ProviderExample {
    public static void main(String[] args) {
        LoggerManager.logSetting();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("pub.tbc.example.provider");
        System.out.println("provider started...");

    }

    @Bean("helloRpc")
    public ProviderFactoryBean helloRpc() {
        log.info("初始化 ProviderFactoryBean - helloRpc");
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

    @Bean("secondService")
    public ProviderFactoryBean secondService() {
        log.info("初始化 ProviderFactoryBean - secondService");
        return ProviderFactoryBean.builder()
                .appKey("rpc-example-provider")
                .serviceItf(SecondService.class)
                .serviceObject(new SecondServiceImpl())
                .weight(1)
                .groupName("default-group")
                .workerThreads(11)
                .serverPort("8081")
                .timeout(3000)
                .build();
    }

    @Bean("haService")
    public ProviderFactoryBean haService() {
        log.info("初始化 ProviderFactoryBean - secondService");
        return ProviderFactoryBean.builder()
                .appKey("rpc-example-provider")
                .serviceItf(HaService.class)
                .serviceObject(new HaServiceImpl())
                .weight(1)
                .groupName("default-group")
                .workerThreads(11)
                .serverPort("8081")
                .timeout(3000)
                .build();
    }
}
