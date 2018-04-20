package pub.tbc.rpc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by tbc on 2018/4/20.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    //UUID,唯一标识一次返回值
    private String uniqueKey;
    //客户端指定的服务超时时间
    private long invokeTimeout;
    //接口调用返回的结果对象
    private Object result;
}
