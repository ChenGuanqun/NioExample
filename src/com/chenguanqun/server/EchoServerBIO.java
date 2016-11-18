package com.chenguanqun.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class EchoServerBIO implements Runnable{
	static int port = 4333;
	Socket socket = null;
	public static void main (String[] args){
		try {
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(port));
			while(true){
				Socket socket = serverSocket.accept();
				new Thread(new EchoServerBIO(socket)).start();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public EchoServerBIO(Socket socket) {
		this.socket= socket;
	}
	
	@Override
	public void run() {
		byte[] buffer = new byte[64];
		try {
			socket.getInputStream().read(buffer);
			System.out.println("data received :"+ new String(buffer));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
