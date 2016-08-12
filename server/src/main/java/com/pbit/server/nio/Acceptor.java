package com.pbit.server.nio;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable {

	private ServerSocketChannel _ServerChannel;
	private SlaveSelector[] _SlaveSelectors ;
	public Acceptor(ServerSocketChannel serverchannel,int size)  {
		_ServerChannel = serverchannel;
		_SlaveSelectors = new SlaveSelector[size];
		for(int i=0; i<size;i++){
			_SlaveSelectors[i] = new SlaveSelector();
			_SlaveSelectors[i].start();
		}
	}

	public void run() {
		try {
			SocketChannel socketChannel = _ServerChannel.accept();
			if(socketChannel !=null){
				new Handler();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
