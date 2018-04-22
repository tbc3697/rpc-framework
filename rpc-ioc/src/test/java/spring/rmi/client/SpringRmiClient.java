package spring.rmi.client;

import domain.Account;
import domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import spring.service.AccountService;
import spring.service.UserService;

/**
 * Created by tbc on 2018/4/22.
 */
@Configuration
public class SpringRmiClient {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("spring.rmi.client");
        UserService userService = context.getBean("userRmiProxyFactoryBean", UserService.class);
        User user = userService.findUserByName("susan");
        System.out.println(user);

        AccountService accountService = context.getBean("accountRmiProxyFactoryBean", AccountService.class);
        Account account = Account.builder().name("test1").build();
        accountService.insertAccount(account);
        System.out.println(accountService.getAccounts("test1"));

    }

    @Bean("accountRmiProxyFactoryBean")
    public RmiProxyFactoryBean accountRmiProxyFactoryBean() {
        RmiProxyFactoryBean accountRmiProxyFactoryBean = new RmiProxyFactoryBean();
        accountRmiProxyFactoryBean.setServiceUrl("rmi://127.0.0.1:1101/accountRmiService");
        accountRmiProxyFactoryBean.setServiceInterface(AccountService.class);
        accountRmiProxyFactoryBean.afterPropertiesSet();
        return accountRmiProxyFactoryBean;
    }

    @Bean("userRmiProxyFactoryBean")
    public RmiProxyFactoryBean userRmiProxyFactoryBean() {
        RmiProxyFactoryBean userRmiProxyFactoryBean = new RmiProxyFactoryBean();
        userRmiProxyFactoryBean.setServiceUrl("rmi://127.0.0.1:1101/userRmiService");
        userRmiProxyFactoryBean.setServiceInterface(UserService.class);
        userRmiProxyFactoryBean.afterPropertiesSet();
        return userRmiProxyFactoryBean;
    }


}
