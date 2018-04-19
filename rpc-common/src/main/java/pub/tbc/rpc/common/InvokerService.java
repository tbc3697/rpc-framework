package pub.tbc.rpc.common;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author liyebing created on 17/2/11.
 * @version $Id$
 */
@Data
public class InvokerService implements Serializable {

    private Class<?> serviceItf;
    private Object serviceObject;
    private Method serviceMethod;
    private String invokerIp;
    private int invokerPort;
    private long timeout;
    //服务提供者唯一标识
    private String remoteAppKey;
    //服务分组组名
    private String groupName = "default";

}
