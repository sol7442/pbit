package com.pbit.server;

import java.io.IOException;

import com.pbit.service.Request;
import com.pbit.service.Response;

public abstract class Service {
	public void init(){}
	public void service(Request request, Response response){
		
	}
	abstract public void accept() throws IOException;
	abstract public void receive();
	abstract public void send();
}
