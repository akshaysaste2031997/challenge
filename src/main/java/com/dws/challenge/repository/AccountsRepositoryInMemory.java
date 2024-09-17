package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

	@Override
	public void saveAccount(Account account) {
		/* As we are using in memory database so it is not really required but have still added the method.
		 As we are using objects which works with call by value so we do not need this but in future if we would plan to move to actual DB then we would need this function.
		 As we are using in memory object so it would not be possible but if using DB we can maintain a variable which will 
		 have existing balance i.e. the balance during start of transaction and we have where clause to check if the balance 
		 in DB is same as that of the time transaction started and will proceed only when true else will not do the transaction.*/
		accounts.put(account.getAccountId(), account);
		
	}

	@Override
	public void updateStatus(Account account, boolean status) {
		// TODO Auto-generated method stub
		account.setPending(status);
	}

}
