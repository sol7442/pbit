package com.pbit.server;

import com.pbit.service.Service;


public class Sender implements Runnable {
	private Service service = null;
	public Sender(Service service){
		this.service = service;
	}
	public void run() {
	//	service.send();
	}
}
