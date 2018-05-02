package pub.tbc.rpc.ioc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.common.helper.IPHelper;
import pub.tbc.rpc.remoting.netty.NettyServer;
import pub.tbc.rpc.registry.IRegisterCenter4Provider;
import pub.tbc.rpc.registry.zk.RegisterCenter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tbc on 2018/4/17.
 */
@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
        log.info("类: {}, 方法: {}", getClass(), "afterPropertiesSet");
        // 启动Netty服务端
        NettyServer.singleton().start(Integer.parseInt(serverPort));

        // 注册到Zookeeper，元数据注册中心
        List<ProviderService> providerServiceList = buildProviderServiceInfoS();
        IRegisterCenter4Provider registerCenter4Provider = RegisterCenter.singleton();
        registerCenter4Provider.registerProvider(providerServiceList);
    }

    /**
     * 将服务按方法粒度拆分，获得服务方法粒度的服务列表
     */
    public List<ProviderService> buildProviderServiceInfoS() {
        return Arrays.stream(serviceObject.getClass().getDeclaredMethods()).map(
                method ->
                        ProviderService.builder()
                                .serviceItf(serviceItf)
                                .serviceObject(serviceObject)
                                .serviceMethod(method)
                                .serverIp(IPHelper.localIp())
                                .serverPort(Integer.parseInt(serverPort))
                                .timeout(timeout)
                                .weight(weight)
                                .workerThreads(workerThreads)
                                .appKey(appKey)
                                .groupName(groupName)
                                .build()
        ).collect(Collectors.toList());
    }
}
