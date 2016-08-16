package com.pbit.server.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pbit.server.ServerException;
import com.pbit.server.util.ByteBufferPool;
import com.pbit.service.Service;

public class ReadWriteHandler implements Runnable {

	public final static int ON_READ 	= 1;
	public final static int ON_WRITE	= 2;

	private BlockingQueue<ByteBuffer[]> _WriteQueue = new ArrayBlockingQueue<ByteBuffer[]>(100);
    private SocketChannel _SocketChannel = null;
    private SelectionKey _SelectionKey = null;
    
    public IServiceListener _ServiceListener;

	protected Logger syslog  = LoggerFactory.getLogger("system");
	protected Logger proclog = LoggerFactory.getLogger("process");
	protected Logger errlog  = LoggerFactory.getLogger("error");
	
	public ReadWriteHandler(IServiceListener listener ) throws IOException {
		_ServiceListener = listener;
	}


	public void run() {
        if (_SelectionKey.isReadable()){
                read();
        }
        else if (_SelectionKey.isWritable()){
                write();
        }
	}

	private void write(){
		try {
			ByteBufferPool bufferpool = ByteBufferPool.getInstance();
			ByteBuffer[] buffers = _WriteQueue.poll();
			if(buffers != null){
				try {
					_SocketChannel.write(buffers);
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					for(int i=0;i<buffers.length;i++){
						bufferpool.offer(buffers[i]);
					}
				}
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	private void read(){
		int numBytes = 0;
		try {
			ByteBufferPool bufferpool = ByteBufferPool.getInstance();
			try {
				ByteBuffer buffer = bufferpool.poll();
				numBytes = _SocketChannel.read(buffer);
				if (numBytes == -1) {
					closeSocketChannel();
				}
				if(numBytes >0){
					_ServiceListener.requestArrived(this,buffer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void wakeup(int flag){
		if(ON_READ == flag){
			_SelectionKey.interestOps(SelectionKey.OP_READ);
		}else if(ON_WRITE == flag){
			_SelectionKey.interestOps(SelectionKey.OP_WRITE);
		}
		_SelectionKey.selector().wakeup();	
	}
    
	private void closeSocketChannel(){
		proclog.debug("Connection Closed : {}",_SocketChannel.toString());
		try {
			_SelectionKey.cancel();
			_SocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void result(String result) {
		
		try {
			ByteBufferPool bufferpool = ByteBufferPool.getInstance();
			ByteBuffer[] write_buffer = new ByteBuffer[1];
			for(int i=0; i<write_buffer.length;i++){
				write_buffer[i] = bufferpool.poll();
			}
			
			while(!_WriteQueue.offer(write_buffer)){
				Thread.sleep(1000);
			}
			
		}catch(ServerException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void error(String error) {
		try {
			ByteBufferPool bufferpool = ByteBufferPool.getInstance();
			ByteBuffer[] write_buffer = new ByteBuffer[1];
			for(int i=0; i<write_buffer.length;i++){
				write_buffer[i] = bufferpool.poll();
			}
			
			while(!_WriteQueue.offer(write_buffer)){
				Thread.sleep(1000);				
			}
			
		}catch(ServerException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}


	public void setChannel(SocketChannel channel) {
		_SocketChannel = channel;
	}


	public void setSelectionKey(SelectionKey key) {
		_SelectionKey = key;
	}
	

}
