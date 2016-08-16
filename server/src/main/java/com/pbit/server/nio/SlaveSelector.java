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
            while (!Thread.interrupted()) {
            	synchronized (this) {}
            	
            	_Selector.select();
                Set<SelectionKey> selected = _Selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()){
                    dispatch(it.next());
                }
                selected.clear();
            }
		} catch (IOException e) {
			//errlog.error("{}",e);
		}
	}
	private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) (key.attachment());
        if (r != null)
            r.run();
	}
	public void addCannel(SocketChannel channel,ReadWriteHandler handler) throws IOException {
		synchronized (this) {
			SocketChannel _SocketChannel = channel;
			_SocketChannel.configureBlocking(false);

			_Selector.wakeup();
			
			SelectionKey _SelectionKey = _SocketChannel.register(_Selector, SelectionKey.OP_READ);
			_SelectionKey.attach(handler);
			_SelectionKey.interestOps(SelectionKey.OP_READ);
			
			handler.setChannel(_SocketChannel);
			handler.setSelectionKey(_SelectionKey);
		}
	}
}
