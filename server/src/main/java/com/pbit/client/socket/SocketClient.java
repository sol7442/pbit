package com.pbit.client.socket;

import java.io.IOException;
import java.net.Socket;

import com.pbit.client.Client;

public class SocketClient extends Client{

	private Socket socket = null;
	@Override
	public void connect(String add, int port) throws IOException {
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
	public byte[] request(byte[] data) throws IOException {
		return null;
	}
	
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
}
