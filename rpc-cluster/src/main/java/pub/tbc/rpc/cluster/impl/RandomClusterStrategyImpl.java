package pub.tbc.rpc.cluster.impl;

import pub.tbc.rpc.cluster.ClusterStrategy;
import pub.tbc.rpc.common.ProviderService;

import java.util.List;
import java.util.Random;

/**
 * 随机
 */
public class RandomClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        int MAX_LEN = providerServices.size();
        int index = new Random().nextInt(MAX_LEN - 1);
        return providerServices.get(index);
    }
}
