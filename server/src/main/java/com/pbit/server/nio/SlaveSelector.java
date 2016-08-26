package com.pbit.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pbit.server.ISocketHandler;

public class SlaveSelector extends Thread{

	private Selector _Selector;
	private final int _Index;
	
	private Object selectorLock = new Object();
	
	protected Logger syslog  = LoggerFactory.getLogger("system");
	protected Logger proclog = LoggerFactory.getLogger("process");
	protected Logger errlog  = LoggerFactory.getLogger("error");
	
	
	public SlaveSelector(int i) throws IOException{
		_Index = i;
		_Selector = Selector.open();
		while(!_Selector.isOpen()){
			try {
				wait();
			} catch (InterruptedException e) {
				errlog.error("{}",e);
			}
		}
	}
	public Selector getSelector(){
		return _Selector;
	}
	
	public void run(){
		try {
            while (!Thread.interrupted()) {
        		synchronized (selectorLock) {
        			//proclog.info("Start SlaveSelector Loop {}",_index);
				}
				
				int count = _Selector.select(); // wait
				if(count == 0){continue;}
				
                Set<SelectionKey> selected = _Selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()){
                	SelectionKey key = it.next();
                	it.remove();
                    dispatch(key);
                }
            }
		} catch (IOException e) {
			errlog.error("{}",e);
		}
	}
	private void dispatch(SelectionKey key) {
		ISocketHandler handler = (ISocketHandler)key.attachment();
        if (key.isReadable()){
        	handler.read();
        }
        else if (key.isWritable()){
        	handler.write();
        }
	}
	public void addCannel(SocketChannel channel,SocketChannelHandler handler) throws IOException {
		synchronized (selectorLock) {
			SocketChannel _SocketChannel = channel;
			_SocketChannel.configureBlocking(false);

			_Selector.wakeup();
			
		proclog.debug("add Channel {} & register = befor " ,_Index);
			SelectionKey selectionKey = _SocketChannel.register(_Selector, SelectionKey.OP_READ,handler);
		proclog.debug("add Channel {} & register = after ",_Index);
		
			handler.setSelectionKey(selectionKey);
			handler.setSelector(this);
		}
	}
	public void interestOps(SelectionKey _SelectionKey, int op) {
		synchronized (selectorLock) {
			proclog.debug("interestOps {} ,{} ",_SelectionKey.toString(),_Index);
			_SelectionKey.interestOps(op);
			_Selector.wakeup();
			
		}
	}
}
