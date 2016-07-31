package com.pbit.server;

abstract public class Protocal implements Runnable {
	abstract public void reader();
	abstract public void writer();
}
