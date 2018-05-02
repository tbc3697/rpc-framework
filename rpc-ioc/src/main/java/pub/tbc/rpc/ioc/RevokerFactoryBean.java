package pub.tbc.rpc.ioc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import pub.tbc.rpc.common.InvokerService;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.registry.IRegisterCenter4Invoker;
import pub.tbc.rpc.registry.zk.RegisterCenter;
import pub.tbc.rpc.remoting.netty.NettyChannelPoolFactory;
import pub.tbc.rpc.remoting.netty.RevokerProxyBeanFactory;
import pub.tbc.toolkit.core.EmptyUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by tbc on 2018/5/2.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokerFactoryBean implements FactoryBean, InitializingBean {
    // 服务接口
    private Class<?> targetInterface;
    // 超时时间
    private int timeout;
    // 服务Bean
    private Object serviceObject;
    // 负载均衡策略
    private String loadBalanceName;
    //
    private String remoteAppKey;
    //
    private String groupName = "default";


    @Override
    public Object getObject() {
        return serviceObject;
    }

    @Override
    public Class<?> getObjectType() {
        return targetInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 获取服务注册中心
        IRegisterCenter4Invoker registerCenter4Invoker = RegisterCenter.singleton();

        // 初始化服务提供者列表到本地缓存
        registerCenter4Invoker.initProviderMap(remoteAppKey, groupName);

        // 初始化netty-channel
        Map<String, List<ProviderService>> providerMap = registerCenter4Invoker.getServiceMetaDataMap4Consume();
        EmptyUtil.requireNonNull(providerMap, "service provider list is empty.");
        NettyChannelPoolFactory.instance().initChannelPoolFactory(providerMap);

        // 获取服务提供者代理对象
//        RevokerProxyBeanFactory proxyBeanFactory = RevokerProxyBeanFactory.singleton(targetInterface, timeout, loadBalanceName);
        RevokerProxyBeanFactory proxyBeanFactory = RevokerProxyBeanFactory.of(targetInterface, timeout, loadBalanceName);
        this.serviceObject = proxyBeanFactory.getProxy();

        // 将消费者信息注册到注册中心
        InvokerService invoker = new InvokerService();
        invoker.setServiceItf(targetInterface);
        invoker.setRemoteAppKey(remoteAppKey);
        invoker.setGroupName(groupName);
        registerCenter4Invoker.registerInvoker(invoker);
    }
}
