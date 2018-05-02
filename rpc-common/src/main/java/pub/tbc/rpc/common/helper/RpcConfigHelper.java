package pub.tbc.rpc.common.helper;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.tbc.rpc.framework.serializer.SerializerType;
import pub.tbc.toolkit.core.Closes;
import pub.tbc.toolkit.core.EmptyUtil;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author liyebing created on 17/2/2.
 * @version $Id$
 */
public class RpcConfigHelper {
    private static final Logger logger = LoggerFactory.getLogger(RpcConfigHelper.class);

    private static final String PROPERTY_CLASSPATH = "rpc-simple.properties";
    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final Properties properties = new Properties();

    //ZK服务地址
    private static String zkService;
    //ZK session超时时间
    private static int zkSessionTimeout;
    //ZK connection超时时间
    private static int zkConnectionTimeout;
    //序列化算法类型
    private static SerializerType serializeType;
    //每个服务端提供者的Netty的连接数
    private static int channelConnectSize;
    // 应用名称
    private static String appName;

    //////////////////////////
    @Getter
    private static String loadBalance;


    /**
     * 初始化
     */
    static {
        InputStream is = null;
        try {
            is = RpcConfigHelper.class.getClassLoader().getResourceAsStream(PROPERTY_CLASSPATH);
            if (null == is) {
                // 如果默认配置文件不存在，查找默认的应用配置文件
                is = RpcConfigHelper.class.getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES);
                if (EmptyUtil.isNull(is)) {
                    throw new IllegalStateException(PROPERTY_CLASSPATH + " can not found in the classpath.");
                }
            }
            properties.load(is);

            zkService = properties.getProperty("zk_service");
            zkSessionTimeout = Integer.parseInt(properties.getProperty("zk_sessionTimeout", "500"));
            zkConnectionTimeout = Integer.parseInt(properties.getProperty("zk_connectionTimeout", "500"));
            channelConnectSize = Integer.parseInt(properties.getProperty("channel_connect_size", "10"));
            serializeType = EmptyUtil.requireNonNull(SerializerType.queryByType(properties.getProperty("serialize_type")), "serializeType is null");
            appName = properties.getProperty("app_name");
            loadBalance = properties.getProperty("load_balance");

        } catch (Throwable t) {
            logger.warn("load " + PROPERTY_CLASSPATH + " properties failed.", t);
            throw new RuntimeException(t);
        } finally {
            Closes.close(is);
        }
    }

    public static String getAppName() {
        return appName;
    }


    public static String getZkService() {
        return zkService;
    }

    public static int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public static int getZkConnectionTimeout() {
        return zkConnectionTimeout;
    }

    public static int getChannelConnectSize() {
        return channelConnectSize;
    }

    public static SerializerType getSerializeType() {
        return serializeType;
    }
}
