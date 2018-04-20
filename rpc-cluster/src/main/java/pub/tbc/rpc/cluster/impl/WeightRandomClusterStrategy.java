package pub.tbc.rpc.cluster.impl;

import pub.tbc.rpc.cluster.impl.suport.WeightClusterStrategy;
import pub.tbc.rpc.common.ProviderService;

import java.util.List;

/**
 * Created by tbc on 2018/4/20.
 */
public class WeightRandomClusterStrategy extends RandomClusterStrategyImpl implements WeightClusterStrategy {
    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        return super.select(weight(providerServices));
    }
}
