package com.pbit.policy;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import com.pbit.server.Service;
import com.pbit.server.nio.NioServer;

public class PolicyServer extends NioServer {

	public PolicyServer(int selector_pool_size) {
		super(selector_pool_size);
	}

	@Override
	public Service newService(Selector selector, SelectionKey key) {
		return new PolicyService(selector, key);
	}

}
