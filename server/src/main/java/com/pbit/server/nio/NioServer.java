package com.pbit.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pbit.server.Server;
import com.pbit.server.ServerException;
import com.pbit.server.Service;
import com.pbit.server.Connection;
import com.pbit.server.Receiver;
import com.pbit.server.Sender;
import com.pbit.server.util.ByteBufferPool;
import com.pbit.service.Request;
import com.pbit.service.Response;
import com.pbit.service.ServiceRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class NioServer extends Server {

	private ServerSocketChannel serverchannel;
	private Selector selector;

	private ExecutorService executor = null;
	
	private Logger syslog  = LoggerFactory.getLogger("system");
	private Logger proclog = LoggerFactory.getLogger("process");
	private Logger errlog  = LoggerFactory.getLogger("error");
	
	private Map<String,Connection> connectionMap = new HashMap<String, Connection>(); 
	
	public void run() {
		try {
			while (true) {
				selector.select();// wait
				
				Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
				while (keysIterator.hasNext()) {
					SelectionKey key = keysIterator.next();
					keysIterator.remove();
					
					proclog.debug("[{}]:[{}]",key.channel(),key.interestOps());
					
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
			errlog.error("{}",e);
		}
	}

	private void write(SelectionKey key) {
		SocketChannel sc = (SocketChannel)key.channel();
		executor.execute(new Sender(services.get(sc.socket().toString())));
	}

	private void read(SelectionKey key) {
		SocketChannel sc = (SocketChannel)key.channel();
		try {
			ByteBufferPool buffer_pool = ByteBufferPool.getInstance();
			List<ByteBuffer> buffer_list = new ArrayList<ByteBuffer>();
			Request request 	= null;
			Response response 	= null;
			try{
				int readlen = 0;
				while(true){
					ByteBuffer buffer = buffer_pool.poll();
					buffer_list.add(buffer);
					readlen = sc.read(buffer);
					if(readlen <=0){
						break;
					}
				}
				if(readlen == -1){
					closeConnection(key);
				}else{
					
					Connection connection = connectionMap.get(sc.socket().toString());
					request  = newRequest(buffer_list);
					response = newResponse(request);
					executor.execute(newService(connection,request,response));
				}
				
			}catch (IOException e) {
				syslog.error("{}",e);
			}finally{
				for(ByteBuffer buffer : buffer_list ){
					buffer_pool.offer(buffer);
				}
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	public abstract Runnable newService(Connection connection, Request request, Response response);
	public abstract Response newResponse(Request request);
	public abstract Request newRequest(List<ByteBuffer> buffer_list);
	private void closeConnection(SelectionKey key) {
		// TODO Auto-generated method stub
		
	}

	private void accept(SelectionKey key) throws IOException {		
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);

		proclog.debug("accecpt : {}",sc.socket().toString());
		connectionMap.put(sc.socket().toString(), new NioConnection(sc));
	}

	
	public void receive() {
	}
	@Override
	public void open(int port) throws IOException {
		serverchannel = ServerSocketChannel.open();
		serverchannel.configureBlocking(false);
		serverchannel.socket().bind(new InetSocketAddress("localhost", port));

		selector = Selector.open();
		serverchannel.register(selector, SelectionKey.OP_ACCEPT);
		executor = Executors.newCachedThreadPool();
		
		syslog.info("Server Open : {}",serverchannel.toString());
	}
	abstract public Service newService(Selector selector, SelectionKey key);
}
