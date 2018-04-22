package pub.tbc.rpc.cluster.loadbalance;

import pub.tbc.rpc.common.helper.PropertyConfigHelper;
import pub.tbc.toolkit.core.EmptyUtil;
import pub.tbc.toolkit.core.collect.MapBuilder;
import pub.tbc.rpc.cluster.loadbalance.impl.*;

import java.util.Map;

import static pub.tbc.rpc.cluster.loadbalance.LoadBalanceEnum.*;

/**
 * Created by tbc on 2018/4/20.
 */
public class LoadBalanceEngine {
    private static final Map<LoadBalanceEnum, LoadBalance> clusterStrategyMap; // = Maps.newConcurrentHashMap();

    static {
        clusterStrategyMap = new MapBuilder<LoadBalanceEnum, LoadBalance>()
                .put(RANDOM, new RandomLoadBalance())
                .put(WRANDOM, new WeightRandomLoadBalance())
                .put(POLLING, new PollingLoadBalance())
                .put(WPOLLING, new WeightPollingLoadBalance())
                .put(HASH, new HashLoadBalance())
                .toConrrentMap();
    }

    public static LoadBalance queryClusterStrategy(String clusterStrategy) {
//        PropertyConfigHelper.getL
        LoadBalanceEnum clusterStrategyEnum = queryByCode(clusterStrategy);
        if (EmptyUtil.isNull(clusterStrategyEnum)) {
            return clusterStrategyMap.get(RANDOM);
        }
        return clusterStrategyMap.get(clusterStrategyEnum);
    }

    // 从配置中读取负载均衡算法，若为空，则默认使用随机算法
    public static LoadBalance queryClusterStrategy() {
        return clusterStrategyMap.get(EmptyUtil.orElse(queryByCode(PropertyConfigHelper.getLoadBalance()), RANDOM));
    }
}
