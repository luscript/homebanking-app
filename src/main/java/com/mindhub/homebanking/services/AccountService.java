package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;

import java.util.List;

public interface AccountService {

    public Account getAccount(Long id);
    public List<Account> getAccounts();
    public AccountDTO getAccountDTO(Account account);
    public List<AccountDTO> getAccountsDTO(List<Account> accounts);
    public void save(Account account);

    public Account findByNumber(String number);
}
