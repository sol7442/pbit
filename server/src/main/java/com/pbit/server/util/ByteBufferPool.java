package com.pbit.server.util;

import java.nio.ByteBuffer;

import com.pbit.util.pool.ObjectPool;


public class ByteBufferPool extends ObjectPool<ByteBuffer> {

	public ByteBufferPool(int size) {
		super(size);
	}

	@Override
	public ByteBuffer create() {
		return ByteBuffer.allocateDirect(1024);
	}
}
