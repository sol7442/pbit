package lib.sol.server.service;

import java.io.IOException;

import lib.sol.server.core.nio.NioServer;
public class SingleReactorServer  {
	
	public static void main(String[] args) {
		NioServer server = new NioServer();
		try {
			server.open(5001);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
