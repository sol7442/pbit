package com.pbit.server;

import java.io.IOException;



abstract public class Acceptor {
	abstract public void open(int port)throws IOException;
	abstract public int accept();
}
