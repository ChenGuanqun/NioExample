package com.chenguanqun.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class MaxConnectionTest implements Runnable{
	/*
	 * It is okay when Max is set to 2000
	 * But, same of the connection failed when it is set to 2500
	 * This means the server is able to accept about 2000-2500 connections almost at a time.
	 * 
	 * Here is my question: as the Nio can handle the connection in a async way, where is the bottleneck in server side?
	 * The candidate answer: Nio can handle connections in one thread, it doesn't mean there is no resource limit.
	 * Nio can keep 2000-2500 tcp connections in one thread, and tcp connections needs also much resources. 
	 * 
	 * The advantage of Nio is that it will suffer from the threads number limitations in one JVM.
	 * 
	 * Besides, something is wrong in server side, because the server may support more connection than 2500.
	 * 
	 * When the number of connections is less than 2000, the nio server can successfully handle all these connections. However,
	 * for BIO server, connection time out happens when connections is about 300. With the increase of the number of connections,
	 * some connection is handled successfully. It is because this simulation test is not sending connections at the same time.
	 * Some connection arrives later and they are accepted by server. 
	 */
	
	public static int MAX = 1000;
	
	public int fails=0;
	
	CountDownLatch latch = new CountDownLatch(MAX);
	
	public void test(){
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(4333), 5000);
			socket.getOutputStream().write(Thread.currentThread().toString().getBytes());
			socket.close();
			System.out.println(Thread.currentThread().getName()+" write sucessfully.");
			latch.countDown();
		} catch (IOException e) {
			
			synchronized(this){
				fails++;
			}
			latch.countDown();
			System.err.println(Thread.currentThread().getName()+" write failed.");
			//e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		test();
	}
	
	@Test
	public void testConnection(){
		MaxConnectionTest myTest = new MaxConnectionTest();
		for(int i=0;i<MAX;i++){
			Thread t = new Thread(myTest);
			t.setName("Thread "+i);
			t.start();
		}
		try {
			myTest.latch.await();
			System.err.println("Total fails :"+ myTest.fails);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
