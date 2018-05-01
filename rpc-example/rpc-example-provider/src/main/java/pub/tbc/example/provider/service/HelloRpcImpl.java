package pub.tbc.example.provider.service;

import org.springframework.stereotype.Service;
import pub.tbc.rpc.example.api.service.HelloRpc;

import java.util.Map;

/**
 * Created by tbc on 2018/4/22.
 */
public class HelloRpcImpl implements HelloRpc {
    @Override
    public String hi() {
        return "hi";
    }

    @Override
    public String hi(String str) {
        return "hi, " + str;
    }

    @Override
    public String hi(Map<String, String> param) {
        return "hi, " + param.toString();
    }
}
