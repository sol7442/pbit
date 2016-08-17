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
		byte[] bytes = new byte[buffer.limit()];
		buffer.get(bytes);
		proclog.debug("createRequest:{}", new String(bytes));
		return new PolicyRequest();
	}

	@Override
	public Response createResponse(Request request) {
		proclog.debug("createRequest:{}", request);
		return new PolicyReponse();
	}

}
