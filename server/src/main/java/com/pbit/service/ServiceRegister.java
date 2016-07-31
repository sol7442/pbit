package com.pbit.service;

public class ServiceRegister {
	private static ServiceRegister instance = null;
	public static ServiceRegister getInstance(){
		if(instance == null){
			instance = new ServiceRegister();
		}
		return instance;
	}
}
