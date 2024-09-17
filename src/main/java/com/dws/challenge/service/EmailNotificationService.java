package com.dws.challenge.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.dws.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailNotificationService implements NotificationService {

  @Override
  //Added async to this method so that the main thread will not be blocked for this operation.
  @Async
  public void notifyAboutTransfer(Account account, String transferDescription) {
    //THIS METHOD SHOULD NOT BE CHANGED - ASSUME YOUR COLLEAGUE WILL IMPLEMENT IT
    log
      .info("Sending notification to owner of {}: {}", account.getAccountId(), transferDescription);
  }

}
