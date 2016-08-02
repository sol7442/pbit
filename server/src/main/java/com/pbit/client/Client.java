package com.pbit.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract public class Client {
	
	private int buffer_size = 1024;
	protected InputStream input;
	protected OutputStream output;
	
	abstract public void open(String add,int port) throws IOException;
	abstract public void close()throws IOException;
	
	public void write(byte[] data) throws IOException{
		output.write(data);
	}
	
	public byte[] read() throws IOException{
		byte[] buffer = new byte[buffer_size];
		input.read(buffer);
		return buffer;
	}
}
