package com.pbit.server;


public class Receiver implements Runnable {
	private Service service = null;
	public Receiver(Service service){
		this.service = service;
	}
	public void run() {
		service.receive();
	}
}
