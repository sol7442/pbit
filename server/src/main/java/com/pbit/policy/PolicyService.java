package com.pbit.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pbit.service.Request;
import com.pbit.service.Response;
import com.pbit.service.Service;


public class PolicyService  extends Service{
	
	private Logger syslog  = LoggerFactory.getLogger("system");
	private Logger proclog = LoggerFactory.getLogger("process");
	private Logger errlog  = LoggerFactory.getLogger("error");
	
	public PolicyService() {
	}

	@Override
	public void service(Request request, Response response) {
		proclog.debug("Reqeust :{}",request);
		proclog.debug("Response :{}",response);
	}

}
