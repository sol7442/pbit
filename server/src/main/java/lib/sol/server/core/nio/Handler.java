package lib.sol.server.core.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Handler implements Runnable {

    final SocketChannel channel;
    final SelectionKey selKey;
 
    static final int READ_BUF_SIZE = 1024;
    static final int WRiTE_BUF_SIZE = 1024;
    ByteBuffer readBuf = ByteBuffer.allocate(READ_BUF_SIZE);
    ByteBuffer writeBuf = ByteBuffer.allocate(WRiTE_BUF_SIZE);
    
	public Handler(Selector selector, SocketChannel sc) throws IOException {
		channel = 	sc;
        channel.configureBlocking(false);
        selKey = channel.register(selector, SelectionKey.OP_READ);
        selKey.attach(this);
        selKey.interestOps(SelectionKey.OP_READ);
	}

	public void run() {
        try {
            if (selKey.isReadable())
                read();
            else if (selKey.isWritable())
                write();
        }
        catch (IOException ex) {
        	System.out.println("channel ?? " + selKey.channel().isOpen());
        	System.out.println("key ?? " + selKey.isValid());
        	System.out.println("key ?? " + selKey.interestOps());
        	
        	if(selKey.channel().isOpen()){
        		try {
					closeChannel();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
	}
	
	 // Process data by echoing input to output
    synchronized void process() {
        byte[] bytes;
 
        readBuf.flip();
        System.out.println("readBuf limit : " + readBuf.limit() );
        System.out.println("readBuf remaining : " + readBuf.remaining() );
        bytes = new byte[readBuf.remaining()];
        readBuf.get(bytes, 0, bytes.length);
        System.out.println("process(): " + new String(bytes, Charset.forName("UTF-8")));
 
        writeBuf = ByteBuffer.wrap(bytes);
 
        System.out.println("writeBuf limit : " + writeBuf.limit() );
        System.out.println("writeBuf remaining : " + writeBuf.remaining() );
        
        // Set the key's interest to WRITE operation
        selKey.interestOps(SelectionKey.OP_WRITE);
    }
 
    synchronized void read() throws IOException {
        int numBytes;
 
        numBytes = channel.read(readBuf);
        System.out.println("read(): #bytes read into 'readBuf' buffer = " + numBytes);
 
            if (numBytes == -1) {
                closeChannel();
                System.out.println("read(): client connection might have been dropped!");
        }
        else {
        	process();
        }
    }
 
    private void closeChannel() throws IOException {
    	selKey.cancel();
        channel.close();
	}

	void write() throws IOException {
        int numBytes = 0;
        try {
        //	writeBuf.flip();
         
        	System.out.println("writeBuf limit : " + writeBuf.limit() );
            System.out.println("writeBuf remaining : " + writeBuf.remaining() );
            
            numBytes = channel.write(writeBuf);
            System.out.println("write(): #bytes read from 'writeBuf' buffer = " + numBytes);
 
        	System.out.println("writeBuf limit : " + writeBuf.limit() );
            System.out.println("writeBuf remaining : " + writeBuf.remaining() );
            
            if (numBytes > 0) {
                readBuf.clear();
                writeBuf.clear();
                // Set the key's interest-set back to READ operation
            }
        }finally{
        	selKey.interestOps(SelectionKey.OP_READ);
        }
    }

}
