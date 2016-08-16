package com.pbit.policy;

import com.pbit.service.Request;
import com.pbit.service.Response;
import com.pbit.service.Service;


public class PolicyService  extends Service{
	public PolicyService() {
	}

	@Override
	public void service(Request request, Response response) {
		System.out.println("Reqeust :"+request);
		System.out.println("Response:"+response);
	}

}
