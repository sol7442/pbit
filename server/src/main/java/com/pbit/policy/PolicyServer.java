package com.pbit.policy;


import java.nio.ByteBuffer;

import com.pbit.server.nio.NioServer;
import com.pbit.service.Request;
import com.pbit.service.Response;
import com.pbit.service.Service;

public class PolicyServer extends NioServer {
	@Override
	public Service newService() {
		return new PolicyService();
	}

	@Override
	public Request createRequest(ByteBuffer buffer) {
		buffer.flip();
		System.out.println("createRequest:" + new String(buffer.array()));
		return new PolicyRequest();
	}

	@Override
	public Response createResponse(Request request) {
		System.out.println("createRequest:" + request);
		return new PolicyReponse();
	}

}
