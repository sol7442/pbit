package com.pbit.client.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.pbit.client.Client;

public class SocketClient extends Client{

	private Socket socket = null;
	@Override
	public void open(String add, int port) throws IOException {
		if(socket == null){
			this.socket = new Socket(add,port);
//			this.input  = socket.getInputStream();
//			this.output = socket.getOutputStream();
		}
	}
	@Override
	public void close() throws IOException {
		if(this.socket != null){
			this.socket.close();
			this.socket = null;
		}
	}
	@Override
	public void request(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public byte[] response() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
}
