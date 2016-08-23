package com.pbit.server.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pbit.server.ServerException;
import com.pbit.server.session.ISession;
import com.pbit.server.util.ByteBufferPool;

public class ReadWriteHandler implements ISession, Runnable {

	public final static int ON_READ 	= 1;
	public final static int ON_WRITE	= 2;

	private String _SessionID;
	private BlockingQueue<ByteBuffer[]> _WriteQueue = new ArrayBlockingQueue<ByteBuffer[]>(100);
    private SelectionKey _SelectionKey = null;
    private SlaveSelector _Selector = null;
    
    public IServiceListener _ServiceListener;
    
	protected Logger syslog  = LoggerFactory.getLogger("system");
	protected Logger proclog = LoggerFactory.getLogger("process");
	protected Logger errlog  = LoggerFactory.getLogger("error");
	
	public ReadWriteHandler(IServiceListener listener )  {
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

	synchronized private void write(){
		try {
			ByteBufferPool bufferpool = ByteBufferPool.getInstance();
			ByteBuffer[] buffers = _WriteQueue.poll();
			if(buffers != null){
				try {
					SocketChannel socketChannel = (SocketChannel) _SelectionKey.channel();
					 
					for(int i=0; i<buffers.length;i++){												
						buffers[i].flip();
						while (true) {
	                        int n = socketChannel.write(buffers[i]) ;
	                        proclog.debug("write : {},{}", n,buffers[i].remaining());
	                        if (n == 0 || buffers[i].remaining() == 0)
                               break ;
						} 
					}
				
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					for(int i=0;i<buffers.length;i++){
						bufferpool.offer(buffers[i]);
					}
				}
			}
			wakeup(ON_READ);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	synchronized private void read(){
		int numBytes = 0;
		try {
			ByteBufferPool bufferpool = ByteBufferPool.getInstance();
			SocketChannel socketChannel = (SocketChannel) _SelectionKey.channel();
			try {
				ByteBuffer buffer = bufferpool.poll();
				numBytes = socketChannel.read(buffer);
				if (numBytes == -1) {
					closeSocketChannel();
				}
				if(numBytes >0){
					_ServiceListener.requestArrived(this,buffer);
				}
			} catch (IOException e) {
				closeSocketChannel();
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void wakeup(int flag){
		if(ON_READ == flag){
			_Selector.interestOps(_SelectionKey,SelectionKey.OP_READ);
		}else if(ON_WRITE == flag){
			_Selector.interestOps(_SelectionKey,SelectionKey.OP_WRITE);
		}
		proclog.debug("Opt : {}",flag);
	}
    
	private void closeSocketChannel(){
		SocketChannel socketChannel = (SocketChannel) _SelectionKey.channel();
		proclog.debug("Connection Closed : {}",socketChannel.toString());
		try {
			_SelectionKey.cancel();
			socketChannel.close();
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
				write_buffer[i].put(result.getBytes());
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

	public void setSelector(SlaveSelector selector){
		_Selector = selector;
	}
	public void setSelectionKey(SelectionKey key) {
		_SelectionKey = key;
	}


	public String getId() {
		return _SessionID;
	}
	public void setId(String id){
		_SessionID = id;
	}
}
