package com.dws.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGlobalException(Exception exception){
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<Object> handleInsufficientBalanceException(InsufficientBalanceException exception){
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException exception){
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<Object> handleAccountNotFoundException(AccountNotFoundException exception){
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
		
	}
	
	@ExceptionHandler(TimeoutException.class)
	public ResponseEntity<Object> handleTimeoutException(TimeoutException exception){
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.REQUEST_TIMEOUT);
		
	}
}
