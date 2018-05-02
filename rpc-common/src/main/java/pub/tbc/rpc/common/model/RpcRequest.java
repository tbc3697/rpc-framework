package pub.tbc.rpc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pub.tbc.rpc.common.ProviderService;

import java.io.Serializable;

/**
 * Created by tbc on 2018/4/19.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RpcRequest implements Serializable {
    // 一次请求的唯标识，使用UUID
    private String uniqueKey;
    // 服务提供者信息
    private ProviderService providerService;
    // 调用的方法名称
    private String invokedMethodName;
    // 参数类型数组
    private Class<?>[] methodParametersType;
    // 传递参数
    private Object[] args;
    // 消费端应用名
    private String appName;
    // 消费请求超时时长
    private long invokeTimeout;
}
