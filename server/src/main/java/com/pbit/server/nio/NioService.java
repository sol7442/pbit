package com.pbit.server.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pbit.server.ServerException;
import com.pbit.server.Service;
import com.pbit.server.util.ByteBufferPool;
import com.pbit.service.Request;
import com.pbit.service.Response;
import com.pbit.service.ServiceRegistry;

abstract public class NioService extends Service{

	protected Selector selector;
	protected SelectionKey key;
	protected String serviceKey;
	
	protected Logger proclog = LoggerFactory.getLogger("process");
	protected Logger syslog = LoggerFactory.getLogger("system");
	
	private ServiceRegistry services = NioServiceRegistry.getInstance();
	
	private ServerSocketChannel ssc = null;
	private SocketChannel sc = null;
	
	public NioService(Selector selector,SelectionKey key) {
		this.selector = selector;
		this.key = key;
	}

	@Override
	public String accept() throws IOException {
		this.ssc = (ServerSocketChannel) key.channel();
		this.sc = ssc.accept();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
		
		serviceKey = sc.socket().toString();
		proclog.debug("accecpt : {}",serviceKey);
		
		return serviceKey;
		
	}
	

	@Override
	public void receive() {
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
					readlen = this.sc.read(buffer);
					if(readlen <=0){
						break;
					}
				}
				if(readlen == -1){
					closeClinet();
				}else{
					request  = newRequest(buffer_list);
					response = newResponse(request);
					service(request, response);
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
	
	public abstract Response newResponse(Request request) ;

	public abstract Request newRequest(List<ByteBuffer> buffer_list);
	
	private void closeClinet() {
		try {
			
			String serviceKey = this.sc.socket().toString();
			
			key.channel().close();
			key.cancel();

			services.remove(serviceKey);
			proclog.debug("close client : {}", serviceKey);

		} catch (IOException e) {
			syslog.error("{}",e);
		}
	}

	@Override
	public void send() {
		
	}

	@Override
	public String getKey() {
		return this.serviceKey;
	}
}
