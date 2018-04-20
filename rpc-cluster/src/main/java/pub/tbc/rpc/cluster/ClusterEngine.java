package pub.tbc.rpc.cluster;

import pub.tbc.rpc.cluster.impl.*;
import pub.tbc.rpc.common.helper.PropertyConfigHelper;
import pub.tbc.toolkit.core.EmptyUtil;
import pub.tbc.toolkit.core.collect.MapBuilder;

import java.util.Map;

import static pub.tbc.rpc.cluster.ClusterStrategyEnum.*;

/**
 * Created by tbc on 2018/4/20.
 */
public class ClusterEngine {
    private static final Map<ClusterStrategyEnum, ClusterStrategy> clusterStrategyMap; // = Maps.newConcurrentHashMap();

    static {
        clusterStrategyMap = new MapBuilder<ClusterStrategyEnum, ClusterStrategy>()
                .put(RANDOM, new RandomClusterStrategyImpl())
                .put(WRANDOM, new WeightRandomClusterStrategy())
                .put(POLLING, new PollingClusterStrategy())
                .put(WPOLLING, new WeightPollingClusterStragegy())
                .put(HASH, new HashClusterStrategy())
                .toConrrentMap();
    }

    public static ClusterStrategy queryClusterStrategy(String clusterStrategy) {
//        PropertyConfigHelper.getL
        ClusterStrategyEnum clusterStrategyEnum = queryByCode(clusterStrategy);
        if (EmptyUtil.isNull(clusterStrategyEnum)) {
            return clusterStrategyMap.get(RANDOM);
        }
        return clusterStrategyMap.get(clusterStrategyEnum);
    }

    // 从配置中读取负载均衡算法，若为空，则默认使用随机算法
    public static ClusterStrategy queryClusterStrategy() {
        return clusterStrategyMap.get(EmptyUtil.orElse(queryByCode(PropertyConfigHelper.getLoadBalance()), RANDOM));
    }
}
