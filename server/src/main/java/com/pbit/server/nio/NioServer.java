package com.pbit.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pbit.server.Server;
import com.pbit.server.Service;
import com.pbit.server.Receiver;
import com.pbit.server.Sender;

abstract public class NioServer extends Server {

	private ServerSocketChannel serverchannel;
	private Selector selector;

	private ExecutorService executor = null;
	private HashMap<SelectionKey,Service> services = new HashMap<SelectionKey, Service>();
	
	public void run() {
		try {
			while (true) {
				selector.select();// wait

				Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
				while (keysIterator.hasNext()) {
					SelectionKey key = keysIterator.next();
					keysIterator.remove();
					
					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					} else if (key.isWritable()) {
						write(key);
					}
				}
			}
		} catch (IOException e) {
			
		}
	}

	private void write(SelectionKey key) {
		Service service = services.get(key);
		executor.execute(new Receiver(service));
	}

	private void read(SelectionKey key) {
		Service service = services.get(key);
		executor.execute(new Sender(service));
	}

	private void accept(SelectionKey key) throws IOException {		
		Service service = newService(selector,key);
		service.accept();
		services.put(key, service);
	}

	@Override
	public void open(int port) throws IOException {
		serverchannel = ServerSocketChannel.open();
		serverchannel.configureBlocking(false);
		serverchannel.socket().bind(new InetSocketAddress("localhost", port));

		selector = Selector.open();
		serverchannel.register(selector, SelectionKey.OP_ACCEPT);
		
		executor = Executors.newCachedThreadPool();
	}
	abstract public Service newService(Selector selector, SelectionKey key);
}
