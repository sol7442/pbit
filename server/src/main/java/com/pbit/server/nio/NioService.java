package com.pbit.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.pbit.service.Service;

public class NioService extends Service {

	private Selector selector;
	private SelectionKey key;
	public NioService(Selector selector,SelectionKey key) {
		this.selector = selector;
		this.key = key;
	}

	@Override
	public void accept() throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);	
	}

	@Override
	public void receive() {
		// TODO Auto-generated method stub

	}

	@Override
	public void send() {
		// TODO Auto-generated method stub

	}

}
