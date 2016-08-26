package com.pbit.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

import com.pbit.client.nio.NioClient;
import com.pbit.client.socket.SocketClient;

public class ConsoleTester {

	private static String target_addr = "127.0.0.1";
	private static int target_port = 5000;
	
	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			String line = "";
			Client client = null;
			boolean bexit = false;
			while( !bexit ){
				System.out.println("input command : ");
				line = reader.readLine();
				
				int command = command(line);
				switch(command){
				case 1:
					client = connenct();
					break;
				case 2:
					disconnect(client);
					client = null;
					break;
				case 3:
					request(client);
					break;
				case 4:
					client = take();
					break;
				case 5:
					put(client);
					break;
				case 100:
					bexit = true;
					break;
				}
			};
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void put(Client client) {
		// TODO Auto-generated method stub
		
	}

	private static Client take() {
		// TODO Auto-generated method stub
		return null;
	}

	private static void request(Client client) {
		
		try {
			byte[] res = client.request("consoltester..".getBytes());
			if(res != null){
				System.out.println("response : " + new String(res));
			}else{
				System.out.println("Response is null");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	private static void disconnect(Client client) {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Client connenct() {
		Client client = new NioClient();
		try {
			client.connect(target_addr,target_port);
			
			while(!client.isConnected()){
				Thread.sleep(500);
				System.out.println("wait--connected");
			}
			System.out.println("connected--->");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return client;
	}

	private static int command(String line) {
		int command = 0;
		if("exit".equals(line)){
			command = 100;
		}else if("connect".equals(line)){
			command = 1;
		}else if("disconnect".equals(line)){
			command = 2;
		}else if("request".equals(line)){
			command = 3;
		}else if("take".equals(line)){
			command = 4;
		}else if("put".equals(line)){
			command = 5;
		}
		
		return command;
	}

}
