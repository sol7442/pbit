package lib.sol.server.core;

import java.io.IOException;

abstract public class Server implements Runnable {
	private Thread thread;
	abstract public boolean isStart();
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
			thread.join(1000);
		}
	}
}
