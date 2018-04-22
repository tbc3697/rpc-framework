package pub.tbc.rpc.cluster.loadbalance.impl;

import pub.tbc.rpc.cluster.loadbalance.impl.suport.WeightLoadBalance;
import pub.tbc.rpc.common.ProviderService;

import java.util.List;

/**
 * Created by tbc on 2018/4/20.
 */
public class WeightRandomLoadBalance extends RandomLoadBalance implements WeightLoadBalance {
    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        return super.select(weight(providerServices));
    }
}
