package com.pbit.client.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
		
	}

	public void run() {
        System.out.println("Client :: started");  
        try {
			_socketChannel = SocketChannel.open();
	        _socketChannel.configureBlocking(false);  
	        _socketChannel.connect(new InetSocketAddress(host, port));  
	          
//	        _selector = Selector.open();  
//	        _selectionKey =  _socketChannel.register(_selector,SelectionKey.OP_READ);
//	        _selector.wakeup();
	        
		} catch (IOException e1) {
			e1.printStackTrace();
		}  

        
		while(!Thread.interrupted()){
			try {
				read();
//				int selected = _selector.select();
//				
//				Iterator<SelectionKey> iter = _selector.selectedKeys().iterator();
//				System.out.println("selected : " + selected);
//				
//				while(iter.hasNext()){
//					SelectionKey key = iter.next();
//					iter.remove();
//					if(key.isReadable()){
//						System.out.println("isReadable : true");
//						read();
//					}
//				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("client end ----");
	}

	private void read() throws IOException, InterruptedException {
		int readNum = _socketChannel.read(readBuf);
		System.out.println("readNum : " + readNum);
		if(readNum > 0){
			byte[] bytes = new byte[readNum];
			readBuf.get(bytes, 0, readNum);
			messages.offer(bytes,1000,TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void request(byte[] data) throws IOException {
		this.writeBuf.clear();
		this.writeBuf = ByteBuffer.wrap(data);
		int writeNum = _socketChannel.write(writeBuf);
		if(writeNum > 0){
			writeBuf.clear();
			readBuf.clear();
		}
		System.out.println("request : " + new String(data));
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
