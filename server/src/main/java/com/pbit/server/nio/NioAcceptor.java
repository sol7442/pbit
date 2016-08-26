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
			_Selectors[i] = new SlaveSelector(i);
			_Selectors[i].start();
		}
		syslog.info("Server Open : {},{}",_ServerChannel.toString(),_Selectors.length);
	}

	@Override
	public int accept() throws IOException{
		int count = 0;
		_AcceptSelector.select();// accept
		Set<SelectionKey> selected = _AcceptSelector.selectedKeys();	
        count = selected.size();
        Iterator<SelectionKey> it = selected.iterator();
        while (it.hasNext()){        	
        	SelectionKey key = it.next();
        	it.remove();
        	if(key.isAcceptable()){
        		SocketChannel channel = _ServerChannel.accept();
        		if (channel != null){
                	SocketChannelHandler handler = new SocketChannelHandler(_Listener);
                	SlaveSelector selector = getSelector();
                	selector.addCannel(channel,handler);
                	
                	_Listener.addNewSession(channel.socket());
                }
        	}
        }
		return count;
	}
	
	private SlaveSelector getSelector(){
        if(_CurrentSelecotor == _SelectorSize){
        	_CurrentSelecotor = 0;
        }
        return _Selectors[_CurrentSelecotor++];
	}
}
