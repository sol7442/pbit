package com.pbit.client.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pbit.client.Client;

public class NioClient extends Client implements Runnable {

	private Thread thread = null;
	private String host;
	private int port;
	
	private SocketChannel _socketChannel;
	private Selector      _selector;
	
	private ByteBuffer readBuf = ByteBuffer.allocate(1024);
	private ByteBuffer writeBuf = ByteBuffer.allocate(1024);
	private final AtomicBoolean connected = new AtomicBoolean(false);
	
	private Future _futuer = new Future();
	
	@Override
	public void connect(String host, int port) throws IOException {
		this.host = host;
		this.port = port;
		System.out.println("client open before-- ");
        thread =  new Thread(this);
        thread.start();
        System.out.println("client open after-- ");
	}

	@Override
	public void close() throws IOException {
		this.thread.interrupt();
		_selector.wakeup();
	}
	
	@Override
	public boolean isConnected() {
		return connected.get();
	}

	public void run() {
        System.out.println("Client :: started");  
        try {
        	_selector = Selector.open();
			_socketChannel = SocketChannel.open();
	        _socketChannel.configureBlocking(false);  
	        _socketChannel.connect(new InetSocketAddress(host, port));  
	        _socketChannel.register(_selector, SelectionKey.OP_CONNECT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}  
        
		try {
			while(!thread.isInterrupted() && _socketChannel.isOpen()) {
				if (_selector.select() > 0) {
					processSelectedKeys(_selector.selectedKeys());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("client end ----");
	}

	private void processSelectedKeys(Set<SelectionKey> selectedKeys) throws IOException {
	    Iterator<SelectionKey> itr = selectedKeys.iterator();
	    while (itr.hasNext()) {
	      SelectionKey key = itr.next();
	      itr.remove();
	      
	      if (key.isReadable()) processRead(key);
	      if (key.isWritable()) processWrite(key);
	      if (key.isConnectable()) processConnect(key);
	      if (key.isAcceptable()) ;
	    }
	}

	private void processConnect(SelectionKey key) throws IOException {
	    SocketChannel ch = (SocketChannel) key.channel();
	    if (ch.finishConnect()) {
	      key.interestOps(key.interestOps() ^ SelectionKey.OP_CONNECT);
	      key.interestOps(key.interestOps() | SelectionKey.OP_READ);
	      connected.set(true);
	    }
		System.out.println("processConnect("+ch.finishConnect()+")");
	}

	private void processWrite(SelectionKey key) throws IOException {
		WritableByteChannel ch = (WritableByteChannel)key.channel();
    	//synchronized (writeBuf) {
    		//writeBuf.flip();
    		int writeNum = 0;
    		System.out.println(writeBuf.remaining());
    		while(writeBuf.remaining() > 0){
    			writeNum += ch.write(writeBuf);
    		}
    		
    		if (writeNum > 0){
    			key.interestOps(SelectionKey.OP_READ);
    		}
    		else if (writeNum == -1) {
    			System.out.println("peer closed write channel");
    			ch.close();
    		}
    		writeBuf.clear();
    		System.out.println("write  ---> ("+writeNum+")");
	   // }
	}

	private void processRead(SelectionKey key) throws IOException {
	    ReadableByteChannel ch = (ReadableByteChannel)key.channel();
	    int bytesOp = 0, bytesTotal = 0;
	    
	    readBuf.clear();
	    while (readBuf.hasRemaining() && (bytesOp = ch.read(readBuf)) > 0) bytesTotal += bytesOp;
	   
	    if (bytesTotal > 0) {
	    	readBuf.flip();
			
	    	byte[] bytes = new byte[bytesTotal];
			readBuf.get(bytes, 0, bytesTotal);
			_futuer.setResult(bytes);
	    }
	    else if (bytesOp == -1) {
	    	System.out.println("peer closed write channel");
	    	ch.close();
	    }
	}

	@Override
	public byte[] request(byte[] data) throws IOException, TimeoutException {
		_futuer.clear();
		if(!connected.get()){throw new IOException("not connected");}
		writeBuf = ByteBuffer.wrap(data);
		if (writeBuf.hasRemaining()) {
			SelectionKey key = _socketChannel.keyFor(_selector);
			key.interestOps(SelectionKey.OP_WRITE);
			_selector.wakeup();
		}
		System.out.println("request : (" + writeBuf.remaining() + ")" + new String(data));
		return _futuer.getResult(_timeout);
	}
	private class Future{
		private AtomicBoolean ready = new AtomicBoolean(false);
		private byte[] data = null;
		
		synchronized public void setResult(byte[] data){
			this.data = data;
			ready.set(true);
			notifyAll();
		}
		synchronized public byte[] getResult(long wait_time) throws TimeoutException{
			while(!ready.get()){
				try {
					wait(5000);
					if(!ready.get()){
						throw new TimeoutException("Timeout Exeception : " + wait_time);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return this.data;
		}
		synchronized public void clear(){
			ready.set(false);
		}
	}
}
