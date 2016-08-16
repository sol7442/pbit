package com.pbit.server.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.pbit.server.ServerException;
import com.pbit.server.util.ByteBufferPool;

public class ReadWriteHandler implements Runnable {

	public final static int ON_READ 	= 1;
	public final static int ON_WRITE	= 2;
	
    final SocketChannel _SocketChannel;
    final SelectionKey _SelectionKey;
    
    public ByteBufferPool _BufferPool;
    public IServiceListener _ServiceListener;
    
	public ReadWriteHandler(IServiceListener listener, Selector selector, SocketChannel socket_channel) throws IOException {
		_SocketChannel = socket_channel;
		_SocketChannel.configureBlocking(false);
 
		_SelectionKey = _SocketChannel.register(selector, SelectionKey.OP_READ);
		_SelectionKey.attach(this);
		_SelectionKey.interestOps(SelectionKey.OP_READ);
		
		try {
			_BufferPool=  ByteBufferPool.getInstance();
		} catch (ServerException e) {
			e.printStackTrace();
		}
		selector.wakeup();
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
		
		
	}

	private void read(){
		int numBytes = 0;
		try {
			ByteBuffer buffer = _BufferPool.poll();
			_SocketChannel.read(buffer);
			if (numBytes == -1) {
				 closeSocketChannel();
	        }
			
			_ServiceListener.requestArrived(this,buffer);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void watkeup(int flag){
		if(ON_READ == flag){
			_SelectionKey.interestOps(SelectionKey.OP_READ);
		}else if(ON_WRITE == flag){
			_SelectionKey.interestOps(SelectionKey.OP_WRITE);
		}
		_SelectionKey.selector().wakeup();	
	}
    
	private void process() {
		
	}
	private void closeSocketChannel(){
		try {
			_SelectionKey.cancel();
			_SocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("read(): client connection might have been dropped!");	
	}

}
