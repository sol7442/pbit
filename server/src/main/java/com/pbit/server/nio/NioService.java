package com.pbit.server.nio;

import java.io.BufferedInputStream;
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
		try {
			ByteBufferPool buffer_pool = ByteBufferPool.getInstance();
			List<ByteBuffer> buffer_list = new ArrayList<ByteBuffer>();
			try{
				SocketChannel sc = (SocketChannel)key.channel();
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
					close();
				}
				
			}catch (IOException e) {
				e.printStackTrace();
			}finally{
				for(ByteBuffer buffer : buffer_list )
				buffer_pool.offer(buffer);
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send() {
		
	}

	@Override
	public SelectionKey getKey() {
		return this.key;
	}
}
