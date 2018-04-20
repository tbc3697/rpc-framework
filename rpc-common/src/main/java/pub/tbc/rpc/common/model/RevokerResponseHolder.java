package pub.tbc.rpc.common.model;

import pub.tbc.toolkit.core.Sleeps;
import pub.tbc.toolkit.core.collect.Maps;
import pub.tbc.toolkit.core.thread.ExecutorFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by tbc on 2018/4/20.
 */
public class RevokerResponseHolder {
    // 服务返回结果的Map
    private static final Map<String, RpcResponseWrapper> responseMap = Maps.newConcurrentHashMap();
    // 清除过期返回结果的线程组
    private static final ExecutorService removeExpireKeyExecutor = ExecutorFactory.newSingleThreadExecutor();

    static {
        // 删除超时未获取到结果的key，防止内存泄漏
        removeExpireKeyExecutor.execute(() -> {
            while (true) {
                try {
                    responseMap.forEach((key, response) -> {
                        if (response.isExpire()) {
                            responseMap.remove(key);
                        }
                        Sleeps.milliseconds(10);
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
//                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 初始化返回结果容器，requestUniqueKey唯一标识本次调用
     */
    public static void initResponseData(String requestUniqueKey) {
        responseMap.put(requestUniqueKey, RpcResponseWrapper.of());
    }

    /**
     * 将netty调用异步结果放入阻塞队列
     */
    public static void putResultValue(RpcResponse response) {
        long currentTime = System.currentTimeMillis();
        RpcResponseWrapper responseWrapper = responseMap.get(response.getUniqueKey());
        responseWrapper.setResponseTime(currentTime);
        responseWrapper.getResponseQueue().add(response);
        responseMap.put(response.getUniqueKey(), responseWrapper);
    }

    /**
     * 从阻塞队列中获取netty异步返回的结果值
     */
    public static RpcResponse getValue(String requestUniqueKey, long timeout) {
        RpcResponseWrapper responseWrapper = responseMap.get(requestUniqueKey);
        try {
            return responseWrapper.getResponseQueue().poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            responseMap.remove(requestUniqueKey);
        }
    }

}
