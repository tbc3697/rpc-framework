package pub.tbc.rpc.example.api.service;

import java.util.Map;

/**
 * Created by tbc on 2018/4/22.
 */
public interface HelloRpc {

    String hi();

    String hi(String str);

    String hi(Map<String, String> param);
}
