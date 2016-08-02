package com.pbit.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


import com.pbit.server.util.ByteBufferPool;

public class ObjectPool implements Runnable{

	private ByteBufferPool buffer_pool = new ByteBufferPool(10);
	
	public static void main(String[] args){
		Thread[] thread_array = new Thread[5];
		for(int i=0; i<thread_array.length; i++){
			thread_array[i] = new Thread(new ObjectPool(i+":"));
			thread_array[i].start();
		}
		
		for(int i=0; i<thread_array.length; i++){
			try {
				thread_array[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("TEST END --");
	}
	private String name = "";
	public ObjectPool(String name){
		this.name = name;
	}
	public void run(){
		int count = 0;
		while(count < 100){
			System.out.println("name " + this.name  + count + ":----");
			count++;
			
			ByteBuffer buffer = buffer_pool.poll();
			try {
				FileInputStream aFile = new FileInputStream("./Log/App/app-2016-08-02_17_1.log");
				FileChannel inChannel = aFile.getChannel();
				
				int read = inChannel.read(buffer);
				buffer.flip();
				
				for (int i = 0; i < read; i++)
	                System.out.print((char) buffer.get());
				System.out.print("\n");
				
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
