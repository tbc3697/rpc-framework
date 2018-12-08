package pub.tbc.rpc.cluster.loadbalance.impl;

import pub.tbc.rpc.cluster.loadbalance.LoadBalance;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.common.helper.IPHelper;

import java.util.List;

/**
 * 源地址哈希算法
 */
public class HashLoadBalance implements LoadBalance {
    /**
     * 扰动函数: 抄自HashMap
     */
    private int hash(int hashCode) {
        return (hashCode == 0) ? 0 : hashCode ^ (hashCode >>> 16);
    }

    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        // 获取调用方IP
        String localIP = IPHelper.localIp();
        // 获取源地址对应的hashcode
        int hashCode = localIP.hashCode();
        // 获取服务器列表大小
        int size = providerServices.size();
        // 使用按位与算法获取下标 - 抄自HashMap
        return providerServices.get((size - 1) & hash(hashCode));
    }
}
