package com.pbit.policy;


import com.pbit.service.Request;

public class PolicyRequest extends Request {
	public PolicyRequest() {
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("PolicyRequest+++");
		return buffer.toString();
	}

}
