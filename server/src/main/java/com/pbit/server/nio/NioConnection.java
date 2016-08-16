package com.pbit.server.nio;

import java.nio.channels.SocketChannel;

import com.pbit.server.Connection;

public class NioConnection extends Connection{
	private SocketChannel socketChannel;
	public NioConnection(SocketChannel socketChannel){
		this.socketChannel = socketChannel;
		setSocket(this.socketChannel.socket());
	}
}
