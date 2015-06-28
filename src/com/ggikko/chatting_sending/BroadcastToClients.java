package com.ggikko.chatting_sending;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_thread.ChattingServerThread;

public class BroadcastToClients {
	
	private String sendData; 
	private int roomNumber;
	private WaitingRoom cst_waitingRoom;
	private DataOutputStream cst_out;
	private SendToClient sendToClient;
	
	public BroadcastToClients(String sendData, int roomNumber,
			WaitingRoom cst_waitingRoom, DataOutputStream cst_out) {
	
		this.sendData = sendData;
		this.roomNumber = roomNumber;
		this.cst_waitingRoom = cst_waitingRoom;
		this.cst_out = cst_out;
	}
	
	public synchronized void broadcast()
			throws IOException {
		ChattingServerThread client;
		Hashtable clients = cst_waitingRoom.getClients(roomNumber);
		Enumeration enumeration = clients.keys();
		while (enumeration.hasMoreElements()) {
			client = (ChattingServerThread) clients.get(enumeration.nextElement());
			sendToClient = new SendToClient(sendData, cst_out);
			sendToClient.send();
		}
	}




}
