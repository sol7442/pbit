package com.pbit.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SlaveSelector extends Thread{

	private Selector _Selector;
	public SlaveSelector() throws IOException{
		_Selector = Selector.open();
	}
	public Selector getSelector(){
		return _Selector;
	}
	
	public void run(){
		try {
			synchronized (this) {}
			
            while (!Thread.interrupted()) {
            	_Selector.select();
                Set<SelectionKey> selected = _Selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()){
                	SelectionKey key = it.next();
                	it.remove();
                    dispatch(key);
                }
            }
		} catch (IOException e) {
			//errlog.error("{}",e);
		}
	}
	private void dispatch(SelectionKey key) {
        ReadWriteHandler handler = (ReadWriteHandler) (key.attachment());
        System.out.println("=== SELECTION KEY =======");
        System.out.println("A:"+key.isAcceptable());
        System.out.println("C:"+key.isConnectable());
        System.out.println("R:"+key.isReadable());
        System.out.println("V:"+key.isValid());
        System.out.println("W:"+key.isWritable());
        
        if (handler != null){
        	handler.run();
        }
	}
	public void addCannel(SocketChannel channel,ReadWriteHandler handler) throws IOException {
		synchronized (this) {
			SocketChannel _SocketChannel = channel;
			_SocketChannel.configureBlocking(false);

			_Selector.wakeup();
			
			SelectionKey _SelectionKey = _SocketChannel.register(_Selector, SelectionKey.OP_READ);
			_SelectionKey.attach(handler);
			_SelectionKey.interestOps(SelectionKey.OP_READ);
			
			handler.setSelectionKey(_SelectionKey);
		}
	}
}
