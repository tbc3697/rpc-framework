package pub.tbc.rpc.cluster.loadbalance.impl;

import pub.tbc.rpc.cluster.loadbalance.impl.suport.WeightLoadBalance;
import pub.tbc.rpc.common.ProviderService;

import java.util.List;

/**
 * 加权源地址哈希
 *
 * @author tbc  by 2018/12/8
 */
public class WeightHashLoadBalance extends HashLoadBalance implements WeightLoadBalance {
    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        return super.select(weight(providerServices));
    }
}
