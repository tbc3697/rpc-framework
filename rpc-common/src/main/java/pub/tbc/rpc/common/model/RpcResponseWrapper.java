package pub.tbc.rpc.common.model;

import lombok.Getter;
import lombok.Setter;
import pub.tbc.toolkit.core.EmptyUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Netty 异步调用返回结果包装类
 */
public class RpcResponseWrapper {
    // 存储返回结果的阻塞队列
    @Getter
    private BlockingQueue<RpcResponse> responseQueue = new ArrayBlockingQueue<>(1);

    // 结果返回时间
    @Getter
    @Setter
    private long responseTime;

    public static RpcResponseWrapper of() {
        return new RpcResponseWrapper();
    }

    /**
     * 计算该返回结果是否已过期
     */
    public boolean isExpire() {
        RpcResponse response = responseQueue.peek();
        if (EmptyUtil.isNull(response)) {
            return false;
        }

        long timeout = response.getInvokeTimeout();
        if ((System.currentTimeMillis() - responseTime) > timeout) {
            return true;
        }

        return false;
    }


}
