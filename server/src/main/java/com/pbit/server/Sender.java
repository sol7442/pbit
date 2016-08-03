package com.pbit.server;


public class Sender<T> implements Runnable {
	private Service<T> service = null;
	public Sender(Service<T> service){
		this.service = service;
	}
	public void run() {
		service.send();
	}
}
