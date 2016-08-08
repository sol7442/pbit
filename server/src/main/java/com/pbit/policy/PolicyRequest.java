package com.pbit.policy;

import java.io.InputStream;

import com.pbit.service.Request;

public class PolicyRequest extends Request {
	public PolicyRequest(InputStream input) {
		this.input = input;
	}

}
