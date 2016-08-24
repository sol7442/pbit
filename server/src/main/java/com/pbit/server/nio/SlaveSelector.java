package com.pbit.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pbit.server.ISocketHandler;

public class SlaveSelector extends Thread{

	//private ReentrantLock selectorLock = new ReentrantLock();
	private Selector _Selector;
	private Object selectorLock = new Object();
	
	protected Logger syslog  = LoggerFactory.getLogger("system");
	protected Logger proclog = LoggerFactory.getLogger("process");
	protected Logger errlog  = LoggerFactory.getLogger("error");
	
	public SlaveSelector() throws IOException{
		_Selector = Selector.open();
	}
	public Selector getSelector(){
		return _Selector;
	}
	
	public void run(){
		try {
			synchronized (selectorLock) {
				proclog.info("Start SlaveSelector Loop");
			}
			
            while (!Thread.interrupted()) {
				_Selector.select(); // wait
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
			
			SelectionKey selectionKey = _SocketChannel.register(_Selector, SelectionKey.OP_READ);
			selectionKey.attach(handler);
			selectionKey.interestOps(SelectionKey.OP_READ);
			handler.setSelectionKey(selectionKey);
			handler.setSelector(this);
		}
	}
	public void interestOps(SelectionKey _SelectionKey, int op) {
		synchronized (selectorLock) {
			_SelectionKey.interestOps(op);
			_Selector.wakeup();
		}
	}
}
