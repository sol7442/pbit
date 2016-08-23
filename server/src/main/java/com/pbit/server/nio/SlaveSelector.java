package com.pbit.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class SlaveSelector extends Thread{

	private ReentrantLock selectorLock = new ReentrantLock();
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
        if (handler != null){
        	handler.run();
        }
	}
	public void addCannel(SocketChannel channel,ReadWriteHandler handler) throws IOException {
		selectorLock.lock();
		try{
			SocketChannel _SocketChannel = channel;
			_SocketChannel.configureBlocking(false);

			_Selector.wakeup();
			SelectionKey selectionKey = _SocketChannel.register(_Selector, SelectionKey.OP_READ);
			selectionKey.attach(handler);
			selectionKey.interestOps(SelectionKey.OP_READ);
			
			;
			handler.setId(String.valueOf(_SocketChannel.socket().toString().hashCode()));			
			handler.setSelectionKey(selectionKey);
			handler.setSelector(this);
		}finally{
			selectorLock.unlock();
		}
	}
	public void interestOps(SelectionKey _SelectionKey, int op) {
		selectorLock.lock();
		try{
			_Selector.wakeup();
			_SelectionKey.interestOps(op);
		}finally{
			selectorLock.unlock();
		}
	}
}
