package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.ProcessLog;
import com.dws.challenge.domain.TransferDTO;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.exception.InvalidRequestException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;
  
  @MockBean
  private NotificationService notificationService;

  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }
  
  @Test
  void transferAmount() {
    String fromAccountId = "Id-1" + System.currentTimeMillis();
    String toAccountId = "Id-2" + System.currentTimeMillis();
    Account fromAccount = new Account(fromAccountId);
    fromAccount.setBalance(new BigDecimal(1000));
    Account toAccount = new Account(toAccountId);
    toAccount.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(fromAccount);
    this.accountsService.createAccount(toAccount);
    TransferDTO transferDTO = new TransferDTO(fromAccountId, toAccountId, new BigDecimal(50));
    ProcessLog processLog = new ProcessLog();
	processLog.init();
    this.accountsService.transferAmount(transferDTO, processLog);
    assertEquals(accountsService.getAccount(toAccountId).getBalance(), new BigDecimal(1050));
    assertEquals(accountsService.getAccount(fromAccountId).getBalance(), new BigDecimal(950));
  }
  
  @Test
  void transferAmount_AccountNotFound() {
    String fromAccountId = "Id-1" + System.currentTimeMillis();
    String toAccountId = "Id-2" + System.currentTimeMillis();
    Account toAccount = new Account(toAccountId);
    toAccount.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(toAccount);
    TransferDTO transferDTO = new TransferDTO(fromAccountId, toAccountId, new BigDecimal(50));
    ProcessLog processLog = new ProcessLog();
	processLog.init();
    try {
        this.accountsService.transferAmount(transferDTO, processLog);
    	fail("Should have failed as sender account is not present");
    }catch (AccountNotFoundException ex) {
    	assertThat(ex.getMessage()).isEqualTo("Sender Account id " + fromAccountId + " does not exist!");
    }
  }
  
  @Test
  void transferAmount_InsufficientBalance() {
    String fromAccountId = "Id-1" + System.currentTimeMillis();
    String toAccountId = "Id-2" + System.currentTimeMillis();
    Account fromAccount = new Account(fromAccountId);
    fromAccount.setBalance(new BigDecimal(1000));
    Account toAccount = new Account(toAccountId);
    toAccount.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(fromAccount);
    this.accountsService.createAccount(toAccount);
    TransferDTO transferDTO = new TransferDTO(fromAccountId, toAccountId, new BigDecimal(1050));
    ProcessLog processLog = new ProcessLog();
	processLog.init();
    try {
    	this.accountsService.transferAmount(transferDTO, processLog);
    	fail("Should have failed as the requested transfer amount is less than balance");
    }catch (InsufficientBalanceException ex) {
    	assertThat(ex.getMessage()).isEqualTo("Account id " + fromAccountId + " does not have enough balance to do this transaction!");
    }
  }
  
  @Test
  void transferAmount_InvalidRequestBalance() {
    String fromAccountId = "Id-1" + System.currentTimeMillis();
    Account fromAccount = new Account(fromAccountId);
    fromAccount.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(fromAccount);
    TransferDTO transferDTO = new TransferDTO(fromAccountId, fromAccountId, new BigDecimal(100));
    ProcessLog processLog = new ProcessLog();
	processLog.init();
    try {
    	this.accountsService.transferAmount(transferDTO, processLog);
    	fail("Should have failed as the requested sender and receiver are same.");
    }catch (InvalidRequestException ex) {
    	assertThat(ex.getMessage()).isEqualTo("Sender Account id " + transferDTO.getAccountFromId() 
		  + " should not be equal to receiver account id " +transferDTO.getAccountToId() +"!");
    }
  }
}
