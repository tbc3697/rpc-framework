package pub.tbc.example.provider.service;

import pub.tbc.rpc.example.api.service.HelloRpc;

import java.util.Map;

/**
 * Created by tbc on 2018/4/22.
 */
public class HelloRpcImpl implements HelloRpc {
    @Override
    public String hi() {
        return null;
    }

    @Override
    public String hi(String str) {
        return null;
    }

    @Override
    public String hi(Map<String, String> param) {
        return null;
    }
}
