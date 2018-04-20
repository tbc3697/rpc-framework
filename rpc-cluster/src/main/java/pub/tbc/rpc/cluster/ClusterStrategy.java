package pub.tbc.rpc.cluster;

import pub.tbc.rpc.common.ProviderService;

import java.util.List;

/**
 * Created by tbc on 2018/4/20.
 */
public interface ClusterStrategy {
    ProviderService select(List<ProviderService> providerServices);
}
