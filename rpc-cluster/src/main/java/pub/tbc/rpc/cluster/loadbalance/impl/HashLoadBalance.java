package pub.tbc.rpc.cluster.loadbalance.impl;

import pub.tbc.rpc.cluster.loadbalance.LoadBalance;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.common.helper.IPHelper;

import java.util.List;

/**
 * 源地址哈希算法
 */
public class HashLoadBalance implements LoadBalance {
    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        // 获取调用方IP
        String localIP = IPHelper.localIp();
        // 获取源地址对应的hashcode
        int hashCode = localIP.hashCode();
        // 获取服务器列表大小
        int size = providerServices.size();
        return providerServices.get(hashCode % size);
    }
}