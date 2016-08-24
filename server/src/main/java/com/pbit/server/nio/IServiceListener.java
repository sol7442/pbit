package com.pbit.server.nio;

import java.net.Socket;
import java.nio.ByteBuffer;

public interface IServiceListener {

	void requestArrived(SocketChannelHandler readWriteHandler, ByteBuffer buffer);

	void addNewSession(Socket socket);

}
