package com.pbit.server.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.pbit.server.ServerException;
import com.pbit.server.util.ByteBufferPool;

public class ReadWriteSelectHandler implements Runnable {

    final SocketChannel _SocketChannel;
    final SelectionKey _SelectionKey;
    
    private ByteBufferPool _BufferPool;
	public ReadWriteSelectHandler(Selector selector, SocketChannel socket_channel) throws IOException {
		_SocketChannel = socket_channel;
		_SocketChannel.configureBlocking(false);
 
		_SelectionKey = _SocketChannel.register(selector, SelectionKey.OP_READ);
		_SelectionKey.attach(this);
		_SelectionKey.interestOps(SelectionKey.OP_READ);
		
		try {
			_BufferPool=  ByteBufferPool.getInstance();
		} catch (ServerException e) {
			e.printStackTrace();
		}
		selector.wakeup();
	}

	public void run() {
        try {
            if (_SelectionKey.isReadable())
                read();
            else if (_SelectionKey.isWritable())
                write();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	private synchronized void write() throws IOException{
		
		
	}

	private synchronized void read()  throws IOException{
		int numBytes = 0;
		ByteBuffer buffer = _BufferPool.poll();
		_SocketChannel.read(buffer);
		if (numBytes == -1) {
			 closeSocketChannel();
        }
		
		//executor.run();
	}
	
	private void closeSocketChannel(){
		try {
			_SelectionKey.cancel();
			_SocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("read(): client connection might have been dropped!");	
	}

}
