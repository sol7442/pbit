package com.pbit.service;

import java.io.IOException;

public abstract class Service {
	public void init(){}
	public void service(Request request, Response response){
		
	}
	abstract public void accept() throws IOException;
	abstract public void receive();
	abstract public void send();
}
