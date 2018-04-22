package spring.service;

import domain.Account;

import java.util.List;

/**
 * Created by tbc on 2018/4/22.
 */
public interface AccountService {

    void insertAccount(Account account);

    List<Account> getAccounts(String name);
}
