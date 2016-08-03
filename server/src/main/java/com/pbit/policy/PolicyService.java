package com.pbit.policy;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import com.pbit.server.nio.NioService;

public class PolicyService extends NioService {
	public PolicyService(Selector selector, SelectionKey key) {
		super(selector, key);
	}

}
