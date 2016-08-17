package com.pbit.util.pool;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ObjectPool<T> {
	private LinkedList<T> list = null; 
	private ReentrantLock lock = new ReentrantLock();
	private int pool_size = 10;
	
	public ObjectPool(){
		this.list = new LinkedList<T>();
	}
	public void setSize(int pool_size){
		this.pool_size = pool_size;
	}
	public void offer(T object) {
		lock.lock();
		try{
			if(this.list.size() < this.pool_size){
				this.list.offer(object);
			}else{
				destroy(object);
			}
		}finally{
			lock.unlock();
		}
	}
	public T poll() {
		lock.lock();
		try{
			T object = this.list.poll();
			if(object == null){
				object = create();
			}
			return clear(object);
		}finally{
			lock.unlock();
		}
	}
	public void removeAll(){
		lock.lock();
		try{
			for(T object : this.list){
				destroy(object);
			}
			this.list.clear();
		}finally{
			lock.unlock();
		}
	}
	public int size(){
		return this.list.size();
	}
	public abstract T clear(T object);
	public abstract T create();
	public abstract void destroy(T object);
}
