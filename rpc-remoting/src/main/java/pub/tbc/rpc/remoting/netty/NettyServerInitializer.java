package pub.tbc.rpc.remoting.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;
import pub.tbc.rpc.common.helper.RpcConfigHelper;

/**
 * @auth tbc on 2018/5/6.
 */
@Slf4j
@Configurable
public class NettyServerInitializer implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        log.info("服务框架初始化过程: spring -> afterPropertiesSet");
        // 启动Netty服务端
        NettyServer.singleton().start(RpcConfigHelper.getServerPort());
    }
}
