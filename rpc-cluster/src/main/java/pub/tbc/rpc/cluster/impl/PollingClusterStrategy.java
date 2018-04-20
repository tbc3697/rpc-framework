package pub.tbc.rpc.cluster.impl;

import pub.tbc.rpc.cluster.ClusterStrategy;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.toolkit.core.EmptyUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 轮洵
 */
public class PollingClusterStrategy implements ClusterStrategy {
    // 计数器
    private int index;
    private Lock lock = new ReentrantLock();

    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        ProviderService service = null;

        try {
            lock.tryLock(10, TimeUnit.MILLISECONDS);
            // 若计数大于服务提供者个数，将计数器归0
            if (index >= providerServices.size()) {
                index = 0;
            }
            service = providerServices.get(index++);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        // 兜底，保证程序健壮性，若未取到服务，直接取第1个
        if (EmptyUtil.isNull(service)) {
            service = providerServices.get(0);
        }
        return service;
    }
}
