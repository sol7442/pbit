package com.pbit.policy;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.List;

import com.pbit.server.nio.NioService;
import com.pbit.service.Request;
import com.pbit.service.Response;
import com.pbit.util.ByteBufferdInputSteam;

public class PolicyService extends NioService {
	public PolicyService(Selector selector, SelectionKey key) {
		super(selector, key);
	}

	@Override
	public Request newRequest(List<ByteBuffer> buffer_list) {
		return new PolicyRequest(new ByteBufferdInputSteam(buffer_list));
	}

	@Override
	public Response newResponse(Request request) {
		return new PolicyReponse(request) ;
	}

}
