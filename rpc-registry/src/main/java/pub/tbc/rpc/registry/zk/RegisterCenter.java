package pub.tbc.rpc.registry.zk;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import pub.tbc.rpc.common.InvokerService;
import pub.tbc.rpc.common.Pair;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.common.helper.IPHelper;
import pub.tbc.rpc.registry.IRegisterCenter4Governance;
import pub.tbc.rpc.registry.IRegisterCenter4Invoker;
import pub.tbc.rpc.registry.IRegisterCenter4Provider;
import pub.tbc.toolkit.core.EmptyUtil;
import pub.tbc.toolkit.core.classes.ClassUtil;
import pub.tbc.toolkit.core.collect.Lists;
import pub.tbc.toolkit.core.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 注册中心实现
 */
public class RegisterCenter implements IRegisterCenter4Invoker, IRegisterCenter4Provider, IRegisterCenter4Governance {
    private static RegisterCenter registerCenter = new RegisterCenter();

    //服务提供者列表,Key:服务提供者接口  value:服务提供者服务方法列表
    private static final Map<String, List<ProviderService>> providerServiceMap = Maps.newConcurrentHashMap();
    //服务端ZK服务元信息,选择服务(第一次直接从ZK拉取,后续由ZK的监听机制主动更新)
    private static final Map<String, List<ProviderService>> serviceMetaDataMap4Consume = Maps.newConcurrentHashMap();

    private static String ZK_SERVICE = getZkService();
    private static int ZK_SESSION_TIME_OUT = getZkConnectionTimeout();
    private static int ZK_CONNECTION_TIME_OUT = getZkConnectionTimeout();
    private static String ROOT_PATH = "/config_register";
    public static String PROVIDER_TYPE = "provider";
    public static String INVOKER_TYPE = "consumer";
    private static volatile ZkClient zkClient = null;


    private RegisterCenter() {
    }

    public static RegisterCenter singleton() {
        return registerCenter;
    }

    @Override
    public void registerProvider(final List<ProviderService> serviceMetaData) {
        if (EmptyUtil.isEmpty(serviceMetaData)) {
            return;
        }

        //连接zk,注册服务
        synchronized (RegisterCenter.class) {
            for (ProviderService provider : serviceMetaData) {
                String serviceItfKey = provider.getServiceItf().getName();

                List<ProviderService> providers = providerServiceMap.get(serviceItfKey);
                if (providers == null) {
                    providers = Lists.newArrayList();
                }
                providers.add(provider);
                providerServiceMap.put(serviceItfKey, providers);
            }

            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }

            //创建 ZK命名空间/当前部署应用APP命名空间/
            String APP_KEY = serviceMetaData.get(0).getAppKey();
            String ZK_PATH = ROOT_PATH + "/" + APP_KEY;
            boolean exist = zkClient.exists(ZK_PATH);
            if (!exist) {
                zkClient.createPersistent(ZK_PATH, true);
            }

            for (Map.Entry<String, List<ProviderService>> entry : providerServiceMap.entrySet()) {
                //服务分组
                String groupName = entry.getValue().get(0).getGroupName();
                //创建服务提供者
                String serviceNode = entry.getKey();
                String servicePath = ZK_PATH + "/" + groupName + "/" + serviceNode + "/" + PROVIDER_TYPE;
                exist = zkClient.exists(servicePath);
                if (!exist) {
                    zkClient.createPersistent(servicePath, true);
                }

                //创建当前服务器节点
                int serverPort = entry.getValue().get(0).getServerPort();//服务端口
                int weight = entry.getValue().get(0).getWeight();//服务权重
                int workerThreads = entry.getValue().get(0).getWorkerThreads();//服务工作线程
                String localIp = IPHelper.localIp();
                String currentServiceIpNode = servicePath + "/" + localIp + "|" + serverPort + "|" + weight + "|" + workerThreads + "|" + groupName;
                exist = zkClient.exists(currentServiceIpNode);
                if (!exist) {
                    //注意,这里创建的是临时节点
                    zkClient.createEphemeral(currentServiceIpNode);
                }

                //监听注册服务的变化,同时更新数据到本地缓存
                zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                    @Override
                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                        if (currentChilds == null) {
                            currentChilds = Lists.newArrayList();
                        }

                        //存活的服务IP列表
                        List<String> activityServiceIpList = currentChilds.stream().map(input -> input.split("|")[0]).collect(Collectors.toList());

                        refreshActivityService(activityServiceIpList);
                    }
                });

            }
        }
    }

    @Override
    public Map<String, List<ProviderService>> getProviderServiceMap() {
        return providerServiceMap;
    }


    @Override
    public void initProviderMap(String remoteAppKey, String groupName) {
        if (EmptyUtil.isEmpty(serviceMetaDataMap4Consume)) {
            serviceMetaDataMap4Consume.putAll(fetchOrUpdateServiceMetaData(remoteAppKey, groupName));
        }
    }

    @Override
    public Map<String, List<ProviderService>> getServiceMetaDataMap4Consume() {
        return serviceMetaDataMap4Consume;
    }

    @Override
    public void registerInvoker(InvokerService invoker) {
        if (invoker == null) {
            return;
        }

        //连接zk,注册服务
        synchronized (RegisterCenter.class) {

            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }
            //创建 ZK命名空间/当前部署应用APP命名空间/
            boolean exist = zkClient.exists(ROOT_PATH);
            if (!exist) {
                zkClient.createPersistent(ROOT_PATH, true);
            }


            //创建服务消费者节点
            String remoteAppKey = invoker.getRemoteAppKey();
            String groupName = invoker.getGroupName();
            String serviceNode = invoker.getServiceItf().getName();
            String servicePath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName + "/" + serviceNode + "/" + INVOKER_TYPE;
            exist = zkClient.exists(servicePath);
            if (!exist) {
                zkClient.createPersistent(servicePath, true);
            }

            //创建当前服务器节点
            String localIp = IPHelper.localIp();
            String currentServiceIpNode = servicePath + "/" + localIp;
            exist = zkClient.exists(currentServiceIpNode);
            if (!exist) {
                //注意,这里创建的是临时节点
                zkClient.createEphemeral(currentServiceIpNode);
            }
        }
    }


    //利用ZK自动刷新当前存活的服务提供者列表数据
    private void refreshActivityService(List<String> serviceIpList) {
        if (serviceIpList == null) {
            serviceIpList = Lists.newArrayList();
        }

        Map<String, List<ProviderService>> currentServiceMetaDataMap = Maps.newHashMap();
        for (Map.Entry<String, List<ProviderService>> entry : providerServiceMap.entrySet()) {
            String key = entry.getKey();
            List<ProviderService> providerServices = entry.getValue();

            List<ProviderService> serviceMetaDataModelList = currentServiceMetaDataMap.get(key);
            if (serviceMetaDataModelList == null) {
                serviceMetaDataModelList = Lists.newArrayList();
            }

            for (ProviderService serviceMetaData : providerServices) {
                if (serviceIpList.contains(serviceMetaData.getServerIp())) {
                    serviceMetaDataModelList.add(serviceMetaData);
                }
            }
            currentServiceMetaDataMap.put(key, serviceMetaDataModelList);
        }
        providerServiceMap.clear();
//        System.out.println("currentServiceMetaDataMap," + JSON.toJSONString(currentServiceMetaDataMap));
        providerServiceMap.putAll(currentServiceMetaDataMap);
    }


    private void refreshServiceMetaDataMap(List<String> serviceIpList) {
        if (serviceIpList == null) {
            serviceIpList = Lists.newArrayList();
        }

        Map<String, List<ProviderService>> currentServiceMetaDataMap = Maps.newHashMap();
        for (Map.Entry<String, List<ProviderService>> entry : serviceMetaDataMap4Consume.entrySet()) {
            String serviceItfKey = entry.getKey();
            List<ProviderService> serviceList = entry.getValue();

            List<ProviderService> providerServiceList = currentServiceMetaDataMap.get(serviceItfKey);
            if (providerServiceList == null) {
                providerServiceList = Lists.newArrayList();
            }

            for (ProviderService serviceMetaData : serviceList) {
                if (serviceIpList.contains(serviceMetaData.getServerIp())) {
                    providerServiceList.add(serviceMetaData);
                }
            }
            currentServiceMetaDataMap.put(serviceItfKey, providerServiceList);
        }

        serviceMetaDataMap4Consume.clear();
        serviceMetaDataMap4Consume.putAll(currentServiceMetaDataMap);
    }


    private Map<String, List<ProviderService>> fetchOrUpdateServiceMetaData(String remoteAppKey, String groupName) {
        final Map<String, List<ProviderService>> providerServiceMap = Maps.newConcurrentHashMap();
        //连接zk
        synchronized (RegisterCenter.class) {
            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }
        }

        //从ZK获取服务提供者列表
        String providePath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName;
        List<String> providerServices = zkClient.getChildren(providePath);

        for (String serviceName : providerServices) {
            String servicePath = providePath + "/" + serviceName + "/" + PROVIDER_TYPE;
            List<String> ipPathList = zkClient.getChildren(servicePath);
            for (String ipPath : ipPathList) {
                String serverIp = ipPath.split("|")[0];
                String serverPort = ipPath.split("|")[1];
                int weight = Integer.parseInt(ipPath.split("|")[2]);
                int workerThreads = Integer.parseInt(ipPath.split("|")[3]);
                String group = ipPath.split("|")[4];

                List<ProviderService> providerServiceList = providerServiceMap.get(serviceName);
                if (providerServiceList == null) {
                    providerServiceList = Lists.newArrayList();
                }
                ProviderService providerService = new ProviderService();

                providerService.setServiceItf(ClassUtil.loadClass(serviceName));

                providerService.setServerIp(serverIp);
                providerService.setServerPort(Integer.parseInt(serverPort));
                providerService.setWeight(weight);
                providerService.setWorkerThreads(workerThreads);
                providerService.setGroupName(group);
                providerServiceList.add(providerService);

                providerServiceMap.put(serviceName, providerServiceList);
            }

            //监听注册服务的变化,同时更新数据到本地缓存
            zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                @Override
                public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                    if (currentChildren == null) {
                        currentChildren = Lists.newArrayList();
                    }
                    currentChildren = currentChildren.stream().map(s -> s.split("|")[0]).collect(Collectors.toList());
                    refreshServiceMetaDataMap(currentChildren);
                }
            });
        }
        return providerServiceMap;
    }

    @Override
    public Pair<List<ProviderService>, List<InvokerService>> queryProvidersAndInvokers(String serviceName, String appKey) {
        //服务消费者列表
        List<InvokerService> invokerServices = Lists.newArrayList();
        //服务提供者列表
        List<ProviderService> providerServices = Lists.newArrayList();

        //连接zk
        if (zkClient == null) {
            synchronized (RegisterCenter.class) {
                if (zkClient == null) {
                    zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
                }
            }
        }

        String parentPath = ROOT_PATH + "/" + appKey;
        //获取 ROOT_PATH + APP_KEY注册中心子目录列表
        List<String> groupServiceList = zkClient.getChildren(parentPath);
        if (EmptyUtil.isEmpty(groupServiceList)) {
            return Pair.of(providerServices, invokerServices);
        }

        for (String group : groupServiceList) {
            String groupPath = parentPath + "/" + group;
            //获取ROOT_PATH + APP_KEY + group 注册中心子目录列表
            List<String> serviceList = zkClient.getChildren(groupPath);
            if (EmptyUtil.isEmpty(serviceList)) {
                continue;
            }
            for (String service : serviceList) {
                //获取ROOT_PATH + APP_KEY + group +service 注册中心子目录列表
                String servicePath = groupPath + "/" + service;
                List<String> serviceTypes = zkClient.getChildren(servicePath);
                if (EmptyUtil.isEmpty(serviceTypes)) {
                    continue;
                }
                for (String serviceType : serviceTypes) {
                    if (Objects.equals(serviceType, PROVIDER_TYPE)) {
                        //获取ROOT_PATH + APP_KEY + group +service+serviceType 注册中心子目录列表
                        String providerPath = servicePath + "/" + serviceType;
                        List<String> providers = zkClient.getChildren(providerPath);
                        if (EmptyUtil.isEmpty(providers)) {
                            continue;
                        }

                        //获取服务提供者信息
                        for (String provider : providers) {
                            String[] providerNodeArr = provider.split("|");

                            ProviderService providerService = new ProviderService();
                            providerService.setAppKey(appKey);
                            providerService.setGroupName(group);
                            providerService.setServerIp(providerNodeArr[0]);
                            providerService.setServerPort(Integer.parseInt(providerNodeArr[1]));
                            providerService.setWeight(Integer.parseInt(providerNodeArr[2]));
                            providerService.setWorkerThreads(Integer.parseInt(providerNodeArr[3]));
                            providerServices.add(providerService);
                        }

                    } else if (Objects.equals(serviceType, INVOKER_TYPE)) {
                        //获取ROOT_PATH + APP_KEY + group +service+serviceType 注册中心子目录列表
                        String invokerPath = servicePath + "/" + serviceType;
                        List<String> invokers = zkClient.getChildren(invokerPath);
                        if (EmptyUtil.isEmpty(invokers)) {
                            continue;
                        }

                        //获取服务消费者信息
                        for (String invoker : invokers) {
                            InvokerService invokerService = new InvokerService();
                            invokerService.setRemoteAppKey(appKey);
                            invokerService.setGroupName(group);
                            invokerService.setInvokerIp(invoker);
                            invokerServices.add(invokerService);
                        }
                    }
                }
            }

        }
        return Pair.of(providerServices, invokerServices);
    }
}
