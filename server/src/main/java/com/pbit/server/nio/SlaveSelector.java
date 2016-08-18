package com.pbit.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SlaveSelector extends Thread{

	private Object selectorLock = new Object();
	private Selector _Selector;
	public SlaveSelector() throws IOException{
		_Selector = Selector.open();
	}
	public Selector getSelector(){
		return _Selector;
	}
	
	public void run(){
		try {
            while (!Thread.interrupted()) {
            	synchronized (selectorLock) {
            		System.out.println("loop = selectorLock");
            	}
            	int sel = _Selector.select();
            	System.out.println("selected : " + sel);
            	
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
		synchronized (selectorLock) {
			SocketChannel _SocketChannel = channel;
			_SocketChannel.configureBlocking(false);

			_Selector.wakeup();
			
			SelectionKey _SelectionKey = _SocketChannel.register(_Selector, SelectionKey.OP_READ);
			_SelectionKey.attach(handler);
			_SelectionKey.interestOps(SelectionKey.OP_READ);
			
			handler.setSelectionKey(_SelectionKey);
			handler.setSelector(this);
		}
	}
	public void interestOps(SelectionKey _SelectionKey, int op) {
		synchronized (selectorLock) {
			_Selector.wakeup();
			_SelectionKey.interestOps(op);
		}
	}
}
