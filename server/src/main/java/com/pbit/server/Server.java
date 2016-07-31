package com.pbit.server;

import java.io.IOException;

abstract public class Server implements Runnable 
{
	private Thread thread = null;
	private int stop_wait_time = 1000;
	

    abstract public void open(int port) throws IOException;
    
	public void start(){
		if(thread != null){
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
}
