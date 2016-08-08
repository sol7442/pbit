package com.pbit.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class ByteBufferdInputSteam extends InputStream {

	private List<ByteBuffer> buffer_list;
	private int index = 1;
	public ByteBufferdInputSteam(List<ByteBuffer> buffer_list) {
		this.buffer_list = buffer_list;
	}

	@Override
	public int read() throws IOException {
		ByteBuffer buffer = buffer_list.get(index);
		if(!buffer.hasRemaining() ){
			if(buffer_list.size() > index){
				index++;
				return read();
			}else{
				return -1;
			}
		}
        return buffer.get() & 0xFF;
	}
}
