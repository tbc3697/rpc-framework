package spring.http.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import spring.service.UserService;

import java.lang.annotation.Annotation;

/**
 * Created by tbc on 2018/4/22.
 */
@Configuration
public class SpringHttpServer {
    @Bean
    public HttpInvokerServiceExporter getHttpInvokerServiceExporter() {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService("userService");
        exporter.setServiceInterface(UserService.class);
        return exporter;
    }

    public static void main(String[] args){
        String[] basePackages = new String[]{"domain", "spring.service", "spring.http.server"};
        new AnnotationConfigApplicationContext();
    }
}
