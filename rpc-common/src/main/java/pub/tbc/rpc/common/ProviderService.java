package pub.tbc.rpc.common;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 服务注册中心的服务提供者注册信息
 *
 * @author liyebing created on 17/2/10.
 * @version $Id$
 */
@Data
public class ProviderService implements Serializable {

    private Class<?> serviceItf;
    private transient Object serviceObject;
    //    @JsonIgnore
    private transient Method serviceMethod;
    private String serverIp;
    private int serverPort;
    private long timeout;
    //该服务提供者权重
    private int weight;
    //服务端线程数
    private int workerThreads;
    //服务提供者唯一标识
    private String appKey;
    //服务分组组名
    private String groupName;

    public ProviderService copy() {
        ProviderService providerService = new ProviderService();
        providerService.setServiceItf(serviceItf);
        providerService.setServiceObject(serviceObject);
        providerService.setServiceMethod(serviceMethod);
        providerService.setServerIp(serverIp);
        providerService.setServerPort(serverPort);
        providerService.setTimeout(timeout);
        providerService.setWeight(weight);
        providerService.setWorkerThreads(workerThreads);
        providerService.setAppKey(appKey);
        providerService.setGroupName(groupName);
        return providerService;
    }

}
