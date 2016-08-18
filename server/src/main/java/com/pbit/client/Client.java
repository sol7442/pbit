package com.pbit.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract public class Client {
	
	abstract public void open(String add,int port) throws IOException;
	abstract public void close()throws IOException;
	abstract public void request(byte[] data) throws IOException;
	abstract public byte[] response()throws IOException;
}
