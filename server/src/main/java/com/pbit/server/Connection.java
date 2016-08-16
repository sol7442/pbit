package com.pbit.server;

import java.net.Socket;

public class Connection {
	private Socket socket;
	private int state;
	
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
