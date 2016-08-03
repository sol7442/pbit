package com.pbit.server.util;

import java.nio.ByteBuffer;

import com.pbit.server.ServerException;
import com.pbit.util.pool.ObjectPool;


public class ByteBufferPool extends ObjectPool<ByteBuffer> {

	private static ByteBufferPool instance = null;
	private int buffer_size;
	
	public static ByteBufferPool getInstance() throws ServerException{
		if(instance == null){
			throw new ServerException("INSTANCE IS NOT INITIALIZED");
		}
		return instance;
	}
	public void initialize(int bsize, int psize){
		setSize(psize);

		this.buffer_size = bsize;
		instance = new ByteBufferPool();
	}
	public ByteBufferPool(){}
	
	@Override
	public ByteBuffer create() {
		return ByteBuffer.allocateDirect(this.buffer_size);
	}

	@Override
	public void destroy(ByteBuffer object) {
		//
	}
}
