package com.pbit.server;

import java.io.IOException;

import com.pbit.server.nio.IServiceListener;



abstract public class Acceptor {
	protected IServiceListener _Listener;
	abstract public void open(int port)throws IOException;
	abstract public int accept() throws IOException;
	public void setListener(IServiceListener listener) {
		_Listener = listener;
	}
}
