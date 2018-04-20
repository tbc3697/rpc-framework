package pub.tbc.rpc.cluster.impl.suport;

import pub.tbc.rpc.cluster.ClusterStrategy;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.toolkit.core.collect.Lists;

import java.util.List;

/**
 * Created by tbc on 2018/4/20.
 */
public interface WeightClusterStrategy extends ClusterStrategy {

    /**
     * 对权重进行处理，返回加权处理后的列表
     */
    default List<ProviderService> weight(List<ProviderService> providerServices) {
        // 存放加权后的服务提供者列表
        List<ProviderService> providerList = Lists.newArrayList();
        providerServices.stream().forEach(provider -> {
            int weight = provider.getWeight();
            for (int i = 0; i < weight; i++) {
                providerList.add(provider.copy());
            }
        });
        return providerList;
    }
}
