package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.ProcessLog;
import com.dws.challenge.domain.TransferDTO;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.exception.InvalidRequestException;
import com.dws.challenge.exception.TimeoutException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  
  private final NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  // As there is no actual DB here but ideally we should annotate this method with @transactional so that ACID properties would be maintained.
  public void transferAmount(TransferDTO transferDTO, ProcessLog processLog) {
	  
	  long start = System.currentTimeMillis();
	  if(transferDTO.getAccountFromId().equals(transferDTO.getAccountToId())) {
		  log.error("TranID : {} : Sender Account id {} should not be equal to receiver account id {}!" , processLog.getUniqReqID(), transferDTO.getAccountFromId(), transferDTO.getAccountToId() );
		  throw new InvalidRequestException("Sender Account id " + transferDTO.getAccountFromId() 
		  + " should not be equal to receiver account id " +transferDTO.getAccountToId() +"!");
	  }
	  Account fromAccount = accountsRepository.getAccount(transferDTO.getAccountFromId());
	  if(fromAccount==null) {
		  log.error("TranID : {} : Sender Account id {} does not exist!" , processLog.getUniqReqID(), transferDTO.getAccountFromId() );
		  throw new AccountNotFoundException("Sender Account id " + transferDTO.getAccountFromId() + " does not exist!");
	  }
	  Account toAccount = accountsRepository.getAccount(transferDTO.getAccountToId());
	  if(toAccount==null) {
		  log.error("TranID : {} : Receiver Account id {} does not exist!" , processLog.getUniqReqID(), transferDTO.getAccountToId() );
		  throw new AccountNotFoundException("Receiver Account id " + transferDTO.getAccountToId() + " does not exist!");
	  }
	  while(toAccount.isPending()||toAccount.isPending()) {
		  try {
			  //We will wait for some time to retry
			  Thread.sleep(10);
			  // Added check so that the transaction will not go in pending state for long.
			  if(System.currentTimeMillis()-start>100) {
				  log.error("TranID : {} : Timeout : Prevoius transaction did not complete." , processLog.getUniqReqID());
				  throw new TimeoutException("Timeout : Prevoius transaction did not complete.");
			  }
		  } catch (InterruptedException e) {
			  
		  }
	  }
	  
	  // Added this condition below considering that existing running transaction can change the balance and we could have enough balance to proceed.
	  if(fromAccount.getBalance().compareTo(transferDTO.getAmount())<0) {
		  log.error("TranID : {} : Account id {} does not have enough balance to do this transaction!", processLog.getUniqReqID(), transferDTO.getAccountFromId() );
		  throw new InsufficientBalanceException("Account id " + transferDTO.getAccountFromId() + " does not have enough balance to do this transaction!");
	  }
	  try {
		  //Updating status(act as lock) so that no other transaction will be able to update the object
		  accountsRepository.updateStatus(toAccount, true);
		  accountsRepository.updateStatus(fromAccount, true);
		  fromAccount.setBalance(fromAccount.getBalance().subtract(transferDTO.getAmount()));
		  toAccount.setBalance(toAccount.getBalance().add(transferDTO.getAmount()));
		  accountsRepository.saveAccount(toAccount);
		  accountsRepository.saveAccount(fromAccount);
		  notificationService.notifyAboutTransfer(fromAccount,"Account has been debited by "+transferDTO.getAmount());
		  notificationService.notifyAboutTransfer(toAccount,"Account has been credited by "+transferDTO.getAmount());
	  } finally {
		  //Updating status(releasing lock) so that other transactions will be able to update the object
		  accountsRepository.updateStatus(toAccount, false);
		  accountsRepository.updateStatus(fromAccount, false);
	  }
	  
	
  }
}
