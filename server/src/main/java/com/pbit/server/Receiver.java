package com.pbit.server;

import com.pbit.service.Service;


public class Receiver implements Runnable {
	private Service service = null;
	public Receiver(Service service){
		this.service = service;
	}
	public void run() {
	//	service.receive();
	}
}
