package com.pbit.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.pbit.server.Server;
import com.pbit.service.Request;
import com.pbit.service.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
http://jeewanthad.blogspot.kr/2013/03/reacter-pattern-explained-part-2.html
http://gee.cs.oswego.edu/dl/cpjslides/nio.pdf
https://jfarcand.wordpress.com/2006/07/19/httpweblogs-java-netblog20060719tricks-and-tips-nio-part-iv-meet-selectors/
*/

abstract public class NioServer extends Server implements IServiceListener {

	
	
	protected Logger syslog  = LoggerFactory.getLogger("system");
	protected Logger proclog = LoggerFactory.getLogger("process");
	protected Logger errlog  = LoggerFactory.getLogger("error");
	
	
	protected int _SelectorSize = 1;
	
	private ServerSocketChannel _ServerChannel;
	private Selector _AcceptSelector;
	private int _CurSel = 0;
	private Selector[] _Selectors;
	public NioServer(){	}
	@Override
	public void open(int port) throws IOException {
		_ServerChannel = ServerSocketChannel.open();
		_ServerChannel.socket().setReuseAddress(true);
		_ServerChannel.socket().bind(new InetSocketAddress(port));
		_ServerChannel.configureBlocking(false);

		_AcceptSelector = Selector.open();
		SelectionKey sk = _ServerChannel.register(_AcceptSelector, SelectionKey.OP_ACCEPT);
		sk.attach(new Acceptor());
		
		_Selectors = new Selector[_SelectorSize];
		for(int i=0;i<_SelectorSize;i++){
			_Selectors[i] = Selector.open();
		}
		
		syslog.info("Server Open : {}",_ServerChannel.toString());
	}
	
	public void run() {
		try {
            while (!Thread.interrupted()) {
            	_AcceptSelector.select();
                Set<SelectionKey> selected = _AcceptSelector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()){
                    dispatch(it.next());
                }
                selected.clear();
            }
		} catch (IOException e) {
			errlog.error("{}",e);
		}
	}
	private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) (key.attachment());
        if (r != null)
            r.run();
	}
	private class Acceptor implements Runnable {
		public void run() {
            try {
                SocketChannel channel = _ServerChannel.accept();
                if (channel != null){
                    new ReadWriteHandler((IServiceListener) this, _Selectors[_CurSel++], channel);
                    if(_CurSel == _SelectorSize){
                    	_CurSel = 0;
                    }
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
		}
	}
	
	public void requestArrived(ReadWriteHandler handler, ByteBuffer buffer) {
		_WorkPool.execute(new Worker(handler,buffer));
	}
	
	private class Worker implements Runnable{
		private ReadWriteHandler handler;
		private ByteBuffer buffer;
		public Worker(ReadWriteHandler handler, ByteBuffer buffer){
			this.handler = handler;
			this.buffer  = buffer;
			
		}
		public void run() {
			
			try{
				Request request = newRequest(buffer);//Request/Response;
				Response response = newResponse(request);
				service(request,response);
			}catch(Exception e){
				
			}
			
			handler.watkeup(ReadWriteHandler.ON_WRITE);
		}
	}
	
	public abstract void service(Request request,Response response);
}
	
