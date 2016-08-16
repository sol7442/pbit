package com.pbit.server.nio;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import com.pbit.service.Service;
import com.pbit.service.ServiceRegistry;

public class NioServiceRegistry extends ServiceRegistry{

	private static NioServiceRegistry instance = null;
	
	private HashMap<String,Service> services = new HashMap<String, Service>();
	
	public static NioServiceRegistry getInstance(){
		if(instance == null){
			instance = new NioServiceRegistry();
		}
		return instance;
	}
	
	@Override
	public void put(Service service) {
		//services.put(service.getKey(), service);
	}

	@Override
	public Service get(String key) {
		return services.get(key);
	}

	@Override
	public void remove(String key) {
		services.remove(key);
	}
}
