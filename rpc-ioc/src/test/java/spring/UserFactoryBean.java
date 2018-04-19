package spring;

import domain.User;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 当在IOC容器中的Bean实现了FactoryBean后，
 * 通过getBean(String BeanName)获取到的Bean对象并不是FactoryBean的实现类对象，而是这个实现类中的getObject()方法返回的对象。
 * 要想获取FactoryBean的实现类，就要getBean(&BeanName)，在BeanName之前加上&。
 */
@Component("user")
public class UserFactoryBean implements FactoryBean<User> {
    private static final User user = new User();

    @Setter
    private String name = "factoryBeanTestName";
    @Setter
    private String email = "factoryBeanTestEmail@tbc.pub";


    @Override
    public User getObject() throws Exception {
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("spring");
        User user = context.getBean("user", User.class);
        System.out.println(user.getName());
        System.out.println(user.getEmail());
        UserFactoryBean bean = context.getBean("&user", UserFactoryBean.class);
        System.out.println(bean);
        System.out.println(bean.name);
    }
}
