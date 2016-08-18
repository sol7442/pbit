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
import java.util.concurrent.atomic.AtomicBoolean;

import com.pbit.client.Client;

public class NioClient extends Client implements Runnable {

	private Thread thread = null;
	private String host;
	private int port;
	
	private SocketChannel _socketChannel;
	private Selector      _selector;
	private SelectionKey  _selectionKey;
	
	private ByteBuffer readBuf = ByteBuffer.allocate(1024);
	private ByteBuffer writeBuf = ByteBuffer.allocate(1024);
	private final AtomicBoolean connected = new AtomicBoolean(false);
	private BlockingQueue<byte[]> messages = new ArrayBlockingQueue<byte[]>(1024);
	
	@Override
	public void open(String host, int port) throws IOException {
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
	    while (readBuf.hasRemaining() && (bytesOp = ch.read(readBuf)) > 0) bytesTotal += bytesOp;

	    if (bytesTotal > 0) {
	    	readBuf.flip();
			
	    	byte[] bytes = new byte[bytesTotal];
			readBuf.get(bytes, 0, bytesTotal);
			try {
				messages.offer(bytes,1000,TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	readBuf.compact();
	    }
	    else if (bytesOp == -1) {
	    	System.out.println("peer closed write channel");
	    	ch.close();
	    }
	}

	@Override
	public void request(byte[] data) throws IOException {
		if(!connected.get()){throw new IOException("not connected");}
		//synchronized (writeBuf) {
			writeBuf = ByteBuffer.wrap(data);
			if (writeBuf.hasRemaining()) {
				SelectionKey key = _socketChannel.keyFor(_selector);
		        key.interestOps( SelectionKey.OP_WRITE);
		        _selector.wakeup();
			}
			System.out.println("request : ("+writeBuf.remaining()+")" + new String(data));
		//}
	}

	@Override
	public byte[] response() throws IOException {
		byte[] response = null;
		try {
			response = messages.poll(1000,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return response;
	}


}
