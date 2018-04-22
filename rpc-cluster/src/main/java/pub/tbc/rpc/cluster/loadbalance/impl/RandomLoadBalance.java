package pub.tbc.rpc.cluster.loadbalance.impl;

import pub.tbc.rpc.cluster.loadbalance.LoadBalance;
import pub.tbc.rpc.common.ProviderService;

import java.util.List;
import java.util.Random;

/**
 * 随机
 */
public class RandomLoadBalance implements LoadBalance {

    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        int MAX_LEN = providerServices.size();
        int index = new Random().nextInt(MAX_LEN - 1);
        return providerServices.get(index);
    }
}
