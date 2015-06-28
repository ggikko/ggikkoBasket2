package com.ggikko.chatting_main;

import java.net.ServerSocket;
import java.net.Socket;

import com.ggikko.chatting_thread.ChattingServerThread;

public class ChattingServer {

	public static final int server_port = 7777; 
	public static final int server_maxclient = 100; 

	public static void main(String[] args) {
		try {
			ServerSocket server_socket = new ServerSocket(server_port);
			System.out.println("서버 소켓 열음");
			while (true) {
				Socket socket = null;
				ChattingServerThread client = null;
				try {
					socket = server_socket.accept(); 
					client = new ChattingServerThread(socket); 
					client.start(); 
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e); 

				}
				try {
					if (socket != null)
						socket.close(); 

				} catch (Exception e) {
					System.out.println(e); 
				} finally {
					socket = null;
				}
			}

		} catch (Exception e) {
			System.out.println(e); 
		}

	}

}
