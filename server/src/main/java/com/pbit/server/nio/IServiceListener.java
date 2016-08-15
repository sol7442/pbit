package com.pbit.server.nio;

import java.nio.ByteBuffer;

public interface IServiceListener {

	void requestArrived(ReadWriteHandler readWriteHandler, ByteBuffer buffer);

}
