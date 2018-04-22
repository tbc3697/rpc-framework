package pub.tbc.rpc.remoting.netty;

import pub.tbc.rpc.cluster.loadbalance.LoadBalance;
import pub.tbc.rpc.cluster.loadbalance.LoadBalanceEngine;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.common.model.RpcRequest;
import pub.tbc.rpc.common.model.RpcResponse;
import pub.tbc.rpc.registry.IRegisterCenter4Invoker;
import pub.tbc.rpc.registry.zk.RegisterCenter;
import pub.tbc.toolkit.core.EmptyUtil;
import pub.tbc.toolkit.core.thread.ExecutorFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by tbc on 2018/4/21.
 */
public class RevokerProxyBeanFactory implements InvocationHandler {
    private ExecutorService fixedThreadPool = null;
    // 服务接口
    private Class<?> targetInterface;
    // 超时时间
    private int timeout;
    // 调用者线程数
    private static int threadWorkerNumber = 10;
    // 负载均衡策略
    private String clusterStrategy;

    public RevokerProxyBeanFactory(Class<?> targetInterface, int consumerTimeout, String clusterStrategy) {
        this.targetInterface = targetInterface;
        this.timeout = consumerTimeout;
        this.clusterStrategy = clusterStrategy;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 服务接口名称
        String serviceKey = targetInterface.getName();
        // 获取某个服务的提供者列表
        IRegisterCenter4Invoker registerCenter4Invoker = RegisterCenter.singleton();
        List<ProviderService> providerServices = registerCenter4Invoker.getServiceMetaDataMap4Consume().get(serviceKey);

        // 选择软负载策略
        LoadBalance loadBalance = LoadBalanceEngine.queryClusterStrategy(clusterStrategy);
        // 按软负载策略，从服务提供者列表中选择一个服务提供者
        ProviderService providerService = loadBalance.select(providerServices);

        // 复制一份服务提供者信息
        ProviderService newProvider = providerService.copy();
        // 设置服务的方法及接口
        newProvider.setServiceItf(targetInterface);
        newProvider.setServiceMethod(method);

        // 构造Request对象
        RpcRequest request = RpcRequest.builder()
                .uniqueKey(UUID.randomUUID().toString() + "-" + Thread.currentThread().getId())
                .providerService(providerService)
                .invokeTimeout(timeout)
                .invokedMethodName(method.getName())
                .args(args)
//                .appName()
                .build();

        try {
            if (EmptyUtil.isNull(fixedThreadPool)) {
                synchronized (RevokerProxyBeanFactory.class) {
                    if (EmptyUtil.isNull(fixedThreadPool)) {
                        fixedThreadPool = ExecutorFactory.newFixedThreadPool(threadWorkerNumber);
                    }
                }
            }

            // 根据服务提供者的IP/PORT，构建
            InetSocketAddress socketAddress = new InetSocketAddress(providerService.getServerIp(), providerService.getServerPort());

            // send invoker
            Future<RpcResponse> responseFuture = fixedThreadPool.submit(RevokerServiceCallable.of(socketAddress, request));
            // 获取调用的返回结果
            RpcResponse response = responseFuture.get(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
            if (EmptyUtil.nonNull(response)) {
                return response.getResult();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{targetInterface},
                this
        );
    }

    private static volatile RevokerProxyBeanFactory singleton;

    // 双重锁定单例
    public static RevokerProxyBeanFactory singleton(Class<?> targetInterface, int consumerTimeout, String clusterStrategy) {
        if (EmptyUtil.isNull(singleton)) {
            synchronized (RevokerProxyBeanFactory.class) {
                if (EmptyUtil.isNull(singleton)) {
                    singleton = new RevokerProxyBeanFactory(targetInterface, consumerTimeout, clusterStrategy);
                }
            }
        }
        return null;
    }
}
