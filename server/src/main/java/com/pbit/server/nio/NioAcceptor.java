package com.pbit.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pbit.server.Acceptor;
import com.pbit.server.session.ISession;

public class NioAcceptor extends Acceptor {

	protected Logger syslog  = LoggerFactory.getLogger("system");
	protected Logger proclog = LoggerFactory.getLogger("process");
	protected Logger errlog  = LoggerFactory.getLogger("error");
	
	private ServerSocketChannel _ServerChannel;
	private Selector _AcceptSelector;
	private SlaveSelector[] _Selectors;
	
	private int _SelectorSize = 3;
	private int _CurrentSelecotor = 0;
	
	@Override
	public void open(int port) throws IOException {
		_ServerChannel = ServerSocketChannel.open();
		_ServerChannel.socket().setReuseAddress(true);
		_ServerChannel.socket().bind(new InetSocketAddress(port));
		_ServerChannel.configureBlocking(false);
		_AcceptSelector = Selector.open();
		_ServerChannel.register(_AcceptSelector, SelectionKey.OP_ACCEPT);
		_Selectors = new SlaveSelector[_SelectorSize];
		for(int i=0;i<_SelectorSize;i++){
			_Selectors[i] = new SlaveSelector();
			_Selectors[i].start();
		}
		syslog.info("Server Open : {}",_ServerChannel.toString());
	}

	@Override
	public int accept() throws IOException{
		int count = 0;
        Set<SelectionKey> selected = _AcceptSelector.selectedKeys();	// accept
        count = selected.size();

        Iterator<SelectionKey> it = selected.iterator();
        while (it.hasNext()){
        	it.remove();
        	SelectionKey key = it.next();
        	if(key.isAcceptable()){
        		SocketChannel channel = _ServerChannel.accept();
        		if (channel != null){
                	ReadWriteHandler handler = new ReadWriteHandler(_Listener);
                	_Selectors[_CurrentSelecotor++].addCannel(channel,handler);
                    if(_CurrentSelecotor == _SelectorSize){
                    	_CurrentSelecotor = 0;
                    }
                    proclog.debug("new Connection Accept : ({}){}",_CurrentSelecotor,channel.toString());
                }
        	}
        }
		return count;
	}
}
