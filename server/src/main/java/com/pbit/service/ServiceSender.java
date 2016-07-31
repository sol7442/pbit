package com.pbit.service;

public class ServiceSender implements Runnable {
	private Service service = null;
	public ServiceSender(Service service){
		this.service = service;
	}
	public void run() {
		service.send();
	}
}
