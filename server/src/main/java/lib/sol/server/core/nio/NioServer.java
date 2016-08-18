package lib.sol.server.core.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import lib.sol.server.core.Server;

public class NioServer extends Server{

	private Selector _selector;
	private ServerSocketChannel _serverChannel;

	public boolean isStart() {
		return false;
	}

	public void open(int port) throws IOException {
		_selector = Selector.open();
		_serverChannel = ServerSocketChannel.open();
		_serverChannel.socket().bind(new InetSocketAddress(port));
		_serverChannel.configureBlocking(false);
		
		SelectionKey sk = _serverChannel.register(_selector, SelectionKey.OP_ACCEPT);
        sk.attach(new Acceptor());
	}

    class Acceptor implements Runnable {
        public void run() {
            try {
                SocketChannel channel = _serverChannel.accept();
                if (channel != null)
                    new Handler(_selector, channel);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
	public void run() {
		try {
            while (!Thread.interrupted()) {
            	System.out.println("Wait Select -- 1's");
                int selected = _selector.select();
                System.out.println("Recevie Select-- : " + selected);
                Iterator<SelectionKey> it = _selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey sk = (SelectionKey) it.next();
                    it.remove();
                    Runnable r = (Runnable) sk.attachment();
                    if (r != null)
                        r.run();
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
	}

}
