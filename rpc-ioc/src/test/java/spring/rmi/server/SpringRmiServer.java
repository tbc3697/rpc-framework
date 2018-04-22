package spring.rmi.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;
import spring.service.AccountService;
import spring.service.AccountServiceImpl;
import spring.service.UserService;
import spring.service.UserServiceImpl;

/**
 * Created by tbc on 2018/4/22.
 */
@Configuration
public class SpringRmiServer {
    public static void main(String[] args) {
        String[] basePackages = new String[]{"domain", "spring.service", "spring.rmi.server"};
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(basePackages);
//        context.getBean("rmiServiceExporter");


    }

    static Logger logger = LoggerFactory.getLogger(SpringRmiServer.class);


    @Bean("userRmiServiceExporter")
    public RmiServiceExporter userRmiServiceExporter() {
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("userRmiService");
        rmiServiceExporter.setService(new UserServiceImpl());
        rmiServiceExporter.setServiceInterface(UserService.class);
        rmiServiceExporter.setRegistryPort(1101);
        return rmiServiceExporter;
    }

    @Bean("accountRimServiceExporter")
    public RmiServiceExporter accountRimServiceExporter(){
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        rmiServiceExporter.setServiceName("accountRmiService");
        rmiServiceExporter.setService(new AccountServiceImpl());
        rmiServiceExporter.setServiceInterface(AccountService.class);
        rmiServiceExporter.setRegistryPort(1101);
        return rmiServiceExporter;
    }
}
