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
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pbit.server.Server;
import com.pbit.server.ServerException;
import com.pbit.server.Service;
import com.pbit.server.Receiver;
import com.pbit.server.Sender;
import com.pbit.server.util.ByteBufferPool;
import com.pbit.service.Request;
import com.pbit.service.Response;
import com.pbit.service.ServiceRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//executor = Executors.newCachedThreadPool();
/*
http://jeewanthad.blogspot.kr/2013/03/reacter-pattern-explained-part-2.html
http://gee.cs.oswego.edu/dl/cpjslides/nio.pdf
https://jfarcand.wordpress.com/2006/07/19/httpweblogs-java-netblog20060719tricks-and-tips-nio-part-iv-meet-selectors/
*/
abstract public class NioServer extends Server {

	private ServerSocketChannel serverchannel;
	private Selector selector;

//	private ExecutorService executor = null;
	
	private Logger syslog  = LoggerFactory.getLogger("system");
	private Logger proclog = LoggerFactory.getLogger("process");
	private Logger errlog  = LoggerFactory.getLogger("error");
	
	private Map<String,SocketChannel> SocketChannelMap = new HashMap<String, SocketChannel>(); 
	
	protected final int _SelectorPoolSize;
	public NioServer(int selector_pool_size){
		_SelectorPoolSize = selector_pool_size;
	}
	@Override
	public void open(int port) throws IOException {
		serverchannel = ServerSocketChannel.open();
		serverchannel.socket().setReuseAddress(true);
		serverchannel.socket().bind(new InetSocketAddress(port));
		serverchannel.configureBlocking(false);

		selector = Selector.open();
		SelectionKey sk = serverchannel.register(selector, SelectionKey.OP_ACCEPT);
		sk.attach(new Acceptor(serverchannel));
		
		syslog.info("Server Open : {}",serverchannel.toString());
	}
	
	
	public void run() {
		try {
			while (true) {
				selector.select();// wait
				Set<SelectionKey> selected = selector.selectedKeys();
				Iterator<SelectionKey> keysIterator = selected.iterator();
				while (keysIterator.hasNext()) {
					dispatch(keysIterator.next());
				}
				selected.clear();
			}
		} catch (IOException e) {
			errlog.error("{}",e);
		}
	}

	private void dispatch(SelectionKey key) {
		Runnable work = (Runnable)key.attachment();
		if(work != null){
			work.run();
		}
	}
	
	
//	
//	private void write(SelectionKey key) {
//		SocketChannel sc = (SocketChannel)key.channel();
//		executor.execute(new Sender(services.get(sc.socket().toString())));
//	}
//
//	private void read(SelectionKey key) {
//		SocketChannel sc = (SocketChannel)key.channel();
//		try {
//			ByteBufferPool buffer_pool = ByteBufferPool.getInstance();
//			List<ByteBuffer> buffer_list = new ArrayList<ByteBuffer>();
//			Request request 	= null;
//			Response response 	= null;
//			try{
//				int readlen = 0;
//				while(true){
//					ByteBuffer buffer = buffer_pool.poll();
//					buffer_list.add(buffer);
//					readlen = this.sc.read(buffer);
//					if(readlen <=0){
//						break;
//					}
//				}
//				if(readlen == -1){
//					closeClinet();
//				}else{
//					request  = newRequest(buffer_list);
//					response = newResponse(request);
//					service(request, response);
//				}
//				
//			}catch (IOException e) {
//				syslog.error("{}",e);
//			}finally{
//				for(ByteBuffer buffer : buffer_list ){
//					buffer_pool.offer(buffer);
//				}
//			}
//		} catch (ServerException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void accept(SelectionKey key) throws IOException {		
//		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
//		SocketChannel sc = ssc.accept();
//		sc.configureBlocking(false);
//		sc.register(selector, SelectionKey.OP_READ);
//
//		proclog.debug("accecpt : {}",sc.socket().toString());
//		SocketChannelMap.put(sc.socket().toString(), sc);
//	}
//
//	
//	public void receive() {
//	}

	abstract public Service newService(Selector selector, SelectionKey key);
}
