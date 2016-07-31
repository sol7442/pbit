package com.pbit.service;

public class ServiceReceiver implements Runnable {
	private Service service = null;
	public ServiceReceiver(Service service){
		this.service = service;
	}
	public void run() {
		service.receive();
	}
}
