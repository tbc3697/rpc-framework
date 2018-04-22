package spring.http.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 * Created by tbc on 2018/4/22.
 */
@Configuration
public class SpringHttpClient {
    @Bean
    public HttpInvokerProxyFactoryBean getHttpInvokerProxyFactoryBean() {
        HttpInvokerProxyFactoryBean factoryBean = new HttpInvokerProxyFactoryBean();
        factoryBean.setServiceUrl("http://127.0.0.1:8080/user.httpInvoker");

        return factoryBean;
    }
}
