package spring.service;

import domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.tbc.toolkit.core.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by tbc on 2018/4/22.
 */
@Component("accountService")
@Slf4j
public class AccountServiceImpl implements AccountService {

    private static ReadWriteLock lock = new ReentrantReadWriteLock();
    private static List<Account> accounts = Lists.newArrayList();

    @Override
    public void insertAccount(Account acc) {

        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            accounts.add(acc);
            log.info("receive msg: {}, AccountSize: {}", acc, accounts.size());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<Account> getAccounts(String name) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return accounts.stream().filter(a -> name.equals(a.getName())).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }

    }

}
