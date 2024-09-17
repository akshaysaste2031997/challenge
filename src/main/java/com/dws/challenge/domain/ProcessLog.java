package com.dws.challenge.domain;

import lombok.Data;

@Data
public class ProcessLog {

	long uniqReqID;
	
	public void init(){
		uniqReqID = System.currentTimeMillis();
	}
}
