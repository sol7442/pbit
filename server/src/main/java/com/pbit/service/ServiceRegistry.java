package com.pbit.service;

import com.pbit.server.Service;

public abstract class ServiceRegistry<T> {
	public abstract void put(Service<T> service);
	public abstract Service<T> get(T key);
}
