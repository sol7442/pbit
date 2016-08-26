package com.pbit.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

abstract public class Client {
	protected int _timeout = 1000;
	abstract public void connect(String add,int port) throws IOException;
	abstract public void close()throws IOException;
	abstract public boolean isConnected();
	abstract public byte[] request(byte[] data) throws IOException,TimeoutException;
	
	public void setTimeout(int time){
		_timeout = time;
	}
}
