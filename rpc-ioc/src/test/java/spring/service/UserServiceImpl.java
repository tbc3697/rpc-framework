package spring.service;

import domain.User;
import org.springframework.stereotype.Component;
import pub.tbc.rpc.ioc.annotation.RpcService;
import pub.tbc.toolkit.core.collect.Maps;

import java.util.Map;

/**
 * Created by tbc on 2018/4/22.
 */
@Component("userService")
@RpcService(

)
public class UserServiceImpl implements UserService {
    private static final Map<String, User> userMap = Maps.newHashMap();


    static {
        userMap.put("kongxuan", new User("kongxuan", "longxuan.163.com"));
        userMap.put("susan", new User("susan", "susan.163.com"));

    }

    @Override
    public User findUserByName(String userName) {
        return userMap.get(userName);
    }
}
