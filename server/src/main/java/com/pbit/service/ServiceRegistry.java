package com.pbit.service;


public abstract class ServiceRegistry {
	public abstract void put(Service service);
	public abstract void remove(String key);
	public abstract Service get(String key);
}
