package pub.tbc.example.provider.service;

import pub.tbc.rpc.example.api.service.SecondService;
import pub.tbc.toolkit.core.collect.MapBuilder;

import java.util.Map;

/**
 * Created by tbc on 2018/5/3.
 */
public class SecondServiceImpl implements SecondService {
    @Override
    public Map<String, String> hi(String key) {
        return new MapBuilder<String, String>().put(key, "hi, your value").build();
    }
}
