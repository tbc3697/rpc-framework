package pub.tbc.rpc.test.spring;

import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * Created by tbc on 2018/4/17.
 */
@Data
public class ProviderFactoryBean implements FactoryBean, InitializingBean {
    // 服务接口类型
    private Class<?> serviceItf;
    // 服务实现
    private Object serviceObject;
    // 服务端口
    private String serverPort;
    // 服务超时时间
    private long timeout;
    // 服务提供者唯一标识
    private String appKey;
    // 服务分组名称
    private String groupName = "default";
    // 服务提供者权重，默认1，范围[1-100]
    private int weight = 1;
    // 服务端线程数，默认10个线程
    private int workerThreads = 10;

    // 服务代理对象，暂时没用到
    private Object serviceProxyObject;


    @Override
    public Object getObject() throws Exception {
        return serviceProxyObject;
    }

    @Override
    public Class<?> getObjectType() {
        return serviceItf;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * from InitializingBean
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动Netty服务端
        NettyServer.singleton().start(Integer.parseInt(serverPort));

        // 注册到Zookeeper，元数据注册中心
//        List<ProviderService>

    }
}
