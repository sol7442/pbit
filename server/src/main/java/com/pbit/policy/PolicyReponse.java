package com.pbit.policy;

import com.pbit.service.Request;
import com.pbit.service.Response;

public class PolicyReponse extends Response {

	public PolicyReponse() {
		
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("PolicyReponse+++");
		return buffer.toString();
	}
}
