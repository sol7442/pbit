package com.pbit.util.pool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ObjectPool<T> {
	private BlockingQueue<T> queue = null;
	private ReentrantLock lock = new ReentrantLock();
	private int size = 10;
	
	public ObjectPool(int size){
		this.queue = new ArrayBlockingQueue<T>(size);
		this.size = size;
	}
	public void offer(T object) {
		lock.lock();
		try{
			this.queue.offer(object);
		}finally{
			lock.unlock();
		}
	}
	public T poll() {
		lock.lock();
		try{
			T object = this.queue.peek();
			if(object == null){
				object = create();
			}
			return object;
		}finally{
			lock.unlock();
		}
	}
	public void clear(){
		lock.lock();
		try{
			this.queue.clear();
		}finally{
			lock.unlock();
		}
	}
	public int size(){
		return this.queue.size();
	}
	public abstract T create();
}
