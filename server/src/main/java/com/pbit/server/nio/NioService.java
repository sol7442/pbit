package com.pbit.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pbit.server.Service;

abstract public class NioService extends Service <SelectionKey>{

	protected Selector selector;
	protected SelectionKey key;
	
	protected Logger proclog = LoggerFactory.getLogger("process");
	
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
		
		proclog.debug("accecpt : {}",sc.socket());
	}
	

	@Override
	public void receive() {
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		
	}

	@Override
	public void send() {
		
	}

	@Override
	public SelectionKey getKey() {
		return this.key;
	}
}
