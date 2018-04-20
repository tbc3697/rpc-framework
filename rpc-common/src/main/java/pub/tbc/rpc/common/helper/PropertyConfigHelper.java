package pub.tbc.rpc.common.helper;

import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.tbc.rpc.framework.serializer.SerializerType;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author liyebing created on 17/2/2.
 * @version $Id$
 */
public class PropertyConfigHelper {
    public static void main(String[] args) {
        System.out.println("a");
    }

    private static final Logger logger = LoggerFactory.getLogger(PropertyConfigHelper.class);

    private static final String PROPERTY_CLASSPATH = "rpc.properties";
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
            is = PropertyConfigHelper.class.getResourceAsStream(PROPERTY_CLASSPATH);
            if (null == is) {
                throw new IllegalStateException("rpc.properties can not found in the classpath.");
            }
            properties.load(is);

            zkService = properties.getProperty("zk_service");
            zkSessionTimeout = Integer.parseInt(properties.getProperty("zk_sessionTimeout", "500"));
            zkConnectionTimeout = Integer.parseInt(properties.getProperty("zk_connectionTimeout", "500"));
            channelConnectSize = Integer.parseInt(properties.getProperty("channel_connect_size", "10"));
            String seriType = properties.getProperty("serialize_type");
            serializeType = SerializerType.queryByType(seriType);
            if (serializeType == null) {
                throw new RuntimeException("serializeType is null");
            }
            appName = properties.getProperty("app_name");

            loadBalance = properties.getProperty("loadBalance");

        } catch (Throwable t) {
            logger.warn("load rpc-framework's properties file failed.", t);
            throw new RuntimeException(t);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
