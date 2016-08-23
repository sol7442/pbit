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

import com.pbit.server.Acceptor;
import com.pbit.server.Server;
import com.pbit.server.util.ByteBufferPool;
import com.pbit.service.Request;
import com.pbit.service.Response;
import com.pbit.service.Service;

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
	
	private Acceptor _Acceptor;
	public NioServer(){	}
	
	
	@Override
	public void open(int port) throws IOException {
		_Acceptor = new NioAcceptor();
		_Acceptor.setListener(this);
		_Acceptor.open(port);
	}
	
	public void run() {
		try {
			int count = _Acceptor.accept();
		} catch (IOException e) {
			errlog.error("{}",e);
		}
	}
	private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) (key.attachment());
        if (r != null)
            r.run();
	}
//	private class Acceptor implements Runnable {
//		IServiceListener _Listener;
//		public Acceptor(IServiceListener listener) {
//			_Listener = listener;
//		}
//
//		public void run() {
//            try {
//                SocketChannel channel = _ServerChannel.accept();
//                if (channel != null){
//                	ReadWriteHandler handler = new ReadWriteHandler(_Listener);
//                	_Selectors[_CurSel++].addCannel(channel,handler);
//                	
//                    if(_CurSel == _SelectorSize){
//                    	_CurSel = 0;
//                    }
//                    proclog.debug("new Connection Accept : ({}){}",_CurSel,channel.toString());
//                }
//            }
//            catch (IOException ex) {
//                ex.printStackTrace();
//            }
//		}
//	}
	
	public void requestArrived(ReadWriteHandler handler, ByteBuffer buffer) {
		//_WorkPool.execute(new Worker(handler,buffer));
	}
	
//	private class Worker implements Runnable{
//		private ReadWriteHandler handler;
//		private ByteBuffer buffer;
//		public Worker(ReadWriteHandler handler, ByteBuffer buffer){
//			this.handler = handler;
//			this.buffer  = buffer;
//			
//		}
//		public void run() {
//			try{
//				Request request = createRequest(buffer);
//				Response response = createResponse(request);
//				ByteBufferPool.getInstance().offer(buffer);
//				
//				Service  service = newService();
//				service.service(request, response);
//				
//				handler.result("result");
//			}catch(Exception e){
//				handler.result("Error");
//			}finally{
//				handler.wakeup(ReadWriteHandler.ON_WRITE);
//			}
//		}
//	}
	
	public abstract Service newService();
	public abstract Request createRequest(ByteBuffer buffer);
	public abstract Response createResponse(Request request);
}
	
