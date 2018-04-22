package spring.service;

import domain.User;

/**
 * Created by tbc on 2018/4/22.
 */
public interface UserService {
    User findUserByName(String userName);
}
