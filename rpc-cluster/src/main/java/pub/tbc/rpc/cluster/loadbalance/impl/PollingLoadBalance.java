package pub.tbc.rpc.cluster.loadbalance.impl;

import pub.tbc.rpc.cluster.loadbalance.LoadBalance;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.toolkit.core.EmptyUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 轮洵
 */
public class PollingLoadBalance implements LoadBalance {
    // 计数器
    private AtomicInteger index;
    private Lock lock = new ReentrantLock();

    /**
     * 2018年05月09日 - 修改为只有当计数大于服务提供者个数要将计数器归0时，才需要加锁，计数器改为原子类；
     * 在服务数量多时，性能会好些，单个服务时，性能还不如使用需普通计数器，整个操作全加锁
     */
    @Override
    public ProviderService select(List<ProviderService> providerServices) {
        ProviderService service = null;

        // 若计数大于服务提供者个数，将计数器归0
        if (index.get() >= providerServices.size()) {
            try {
                lock.tryLock(10, TimeUnit.MILLISECONDS);
                index.set(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        service = providerServices.get(index.incrementAndGet());

        // 兜底，保证程序健壮性，若未取到服务，直接取第1个
        if (EmptyUtil.isNull(service)) {
            service = providerServices.get(0);
        }
        return service;
    }
}
