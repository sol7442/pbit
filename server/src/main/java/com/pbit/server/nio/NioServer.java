package com.pbit.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pbit.server.Server;
import com.pbit.service.Service;
import com.pbit.service.ServiceReceiver;
import com.pbit.service.ServiceSender;

public class NioServer extends Server {

	private ServerSocketChannel serverchannel;
	private Selector selector;

	private ExecutorService executor = null;
	
	public void run() {
		try {
			while (true) {
				selector.select();// wait

				Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
				while (keysIterator.hasNext()) {
					SelectionKey key = keysIterator.next();
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
		executor.execute(new ServiceReceiver(key));
	}

	private void read(SelectionKey key) {
		executor.execute(new ServiceSender(key));
	}

	private void accept(SelectionKey key) throws IOException {
		Service service = new NioService(selector,key);
		service.accept();
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

}
