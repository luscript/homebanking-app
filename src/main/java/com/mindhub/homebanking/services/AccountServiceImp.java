package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountServiceImp implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public Account getAccount(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if(account.isPresent()) {
            return account.get();
        } else {
            return null;
        }
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public AccountDTO getAccountDTO(Account account) {
        return new AccountDTO(account);
    }

    @Override
    public List<AccountDTO> getAccountsDTO(List<Account> accounts) {
        return accounts
                .stream()
                .map(AccountDTO :: new)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Account account) {
        accountRepository.save(account);
    }

    @Override
    public Account findByNumber(String number) {
        return accountRepository.findByNumber(number);
    }
}
