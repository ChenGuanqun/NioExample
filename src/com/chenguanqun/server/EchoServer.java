package com.chenguanqun.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/*
 * @ author Chen Tao
 * This is a simple Nio server, accepts connection and print the message received from clients
 */

public class EchoServer {

	static int port = 4333;
	
	
	
	public static void main(String[] args) {
		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			
			ServerSocket socket = ssc.socket();
			InetSocketAddress address = new InetSocketAddress(port);
			socket.bind(address);
			
			Selector selector = Selector.open();
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			while(true){
				int num = selector.select();
				Set selectedKeys = selector.selectedKeys();
				Iterator it = selectedKeys.iterator();
				while(it.hasNext()){
					SelectionKey key = (SelectionKey)it.next();
					if((key.readyOps() & SelectionKey.OP_ACCEPT)
						     == SelectionKey.OP_ACCEPT) {
						System.out.println("receive one connection");
						SocketChannel sc = ((ServerSocketChannel) key
                                .channel()).accept();
						sc.configureBlocking(false);
                        SelectionKey sk = sc.register(selector,
                                SelectionKey.OP_READ);
					}else if ((key.readyOps() & (SelectionKey.OP_READ | SelectionKey.OP_WRITE)) != 0) {
						SocketChannel sc = (SocketChannel)key.channel();
						sc.configureBlocking(false);
						ByteBuffer buffer = ByteBuffer.allocate( 64 );
						int readsize = sc.read(buffer);
						if(readsize>0){
							buffer.flip();
							byte[] bytes = new byte[buffer.remaining()];
							buffer.get(bytes);
							System.out.println("server is ready to read: "+new String(bytes));
						}else if(readsize ==-1){
							key.cancel();
							sc.close();
						}
                    }
				}
				selectedKeys.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
