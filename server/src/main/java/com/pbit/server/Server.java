package com.pbit.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import com.pbit.server.util.ByteBufferPool;

abstract public class Server implements Runnable 
{
	private Thread thread = null;
	private int stop_wait_time = 1000;
	
	protected ExecutorService _WorkPool;
	protected ByteBufferPool _BufferPool;
	
    abstract public void open(int port) throws IOException;
    
	public void start(){
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	public void stop() throws InterruptedException{
		if(thread != null){
			thread.interrupt();
			thread.join(stop_wait_time);
		}
	}
	public void setWorkPool(ExecutorService pool){
		_WorkPool = pool;
	}
	public void setBufferPool(ByteBufferPool pool){
		_BufferPool = pool;
	}
	public ExecutorService getWorkPool(){
		return _WorkPool;
	}
	
}
