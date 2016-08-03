package com.pbit.server;


public class Receiver<T> implements Runnable {
	private Service<T> service = null;
	public Receiver(Service<T> service){
		this.service = service;
	}
	public void run() {
		service.receive();
	}
}
