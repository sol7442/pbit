package com.pbit.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;




import com.pbit.server.ServerException;
import com.pbit.server.util.ByteBufferPool;

public class ObjectPoolTest implements Runnable{

	public static void main(String[] args){
		long start_time = System.currentTimeMillis();
		int thread_size = 10;

		ByteBufferPool buffer_pool = new ByteBufferPool();
		buffer_pool.initialize(10240,thread_size);
		
		Thread[] thread_array = new Thread[thread_size];
		for(int i=0; i<thread_array.length; i++){
			thread_array[i] = new Thread(new ObjectPoolTest(i+":"));
			thread_array[i].start();
		}
		
		for(int i=0; i<thread_array.length; i++){
			try {
				thread_array[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end_time = System.currentTimeMillis();
		System.out.println("TEST END --["+(end_time-start_time)+"] : " + buffer_pool.size());
	}
	private String name = "";
	public ObjectPoolTest(String name){
		this.name = name;
	}
	public void run(){
		int count = 0;
		FileOutputStream bfile =null;
		try {
			bfile = new FileOutputStream("./Log/App/out.log");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		ByteBufferPool buffer_pool = null;
		try {
			buffer_pool = ByteBufferPool.getInstance();
		} catch (ServerException e1) {
			e1.printStackTrace();
		}
		
		
		while(count < 1000){
			System.out.println("name " + this.name  + count + ":----");
			count++;
			
			ByteBuffer buffer = buffer_pool.poll();
			
			try {
				FileInputStream aFile = new FileInputStream("./Log/App/app-2016-08-02_17_1.log");
				FileChannel inChannel = aFile.getChannel();
				
				int read = inChannel.read(buffer);
				buffer.flip();
				for (int i = 0; i < read; i++){
					bfile.getChannel().write(buffer);
				}
				
				inChannel.close();
	            aFile.close();
	            buffer.clear();
	            
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				buffer_pool.offer(buffer);
			}
		}
	}
}
