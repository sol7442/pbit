package lib.sol.server.core.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reactor extends Thread {

	private Logger syslog  = LoggerFactory.getLogger("system");
	private Logger proclog = LoggerFactory.getLogger("process");
	private Logger errlog  = LoggerFactory.getLogger("error");
	
	private Selector _Selector;
	
	public void run(){
		while(_Selector.isOpen()){
			try {
				int selected = _Selector.select();
				if(selected !=0){
					dispatch();
				}else{
					errlog.error("Selected is Zero");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void dispatch() {
		final Iterator<SelectionKey> iter_key = _Selector.selectedKeys().iterator();
		while(iter_key.hasNext()){
			SelectionKey key = iter_key.next();
			iter_key.remove();
			if(validateSelectionKey(key)){
				
			}
		}
	}

	private boolean validateSelectionKey(SelectionKey key) {
		if(!key.isValid()){
			if(key.attachment() != null){
				//TODO -- controller notify
			}else{
				key.cancel();
			}
			return false;
		}
		return true;
	}
}
