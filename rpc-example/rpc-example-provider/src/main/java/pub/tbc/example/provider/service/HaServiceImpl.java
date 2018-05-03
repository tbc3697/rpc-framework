package pub.tbc.example.provider.service;

import pub.tbc.rpc.example.api.service.HaService;

/**
 * Created by tbc on 2018/5/3.
 */
public class HaServiceImpl implements HaService {
    @Override
    public String ha() {
        return "->->-> ha <-<-<-";
    }
}
