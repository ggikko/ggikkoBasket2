package com.ggikko.chatting_command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.BroadcastToClients;

public class RequestSendWord implements ChattingCommand {

	private String receiveData;
	private StringBuffer cst_buffer;
	private int cst_roomNumber;
	private WaitingRoom cst_waitingRoom;
	private DataOutputStream cst_out;
	
	private BroadcastToClients broadcastToClients;

	private static final String SEPARATOR = "|";
	private static final int YES_SENDWORD = 2051;
	
	public RequestSendWord(String receiveData, StringBuffer cst_buffer,
			int cst_roomNumber, WaitingRoom cst_waitingRoom,
			DataOutputStream cst_out) {
		this.receiveData = receiveData;
		this.cst_buffer = cst_buffer;
		this.cst_roomNumber = cst_roomNumber;
		this.cst_waitingRoom = cst_waitingRoom;
		this.cst_out = cst_out;
	}

	@Override
	public void execute() throws IOException {
		
		StringTokenizer st = new StringTokenizer(receiveData, SEPARATOR);
		int command = Integer.parseInt(st.nextToken());
		
		String id = st.nextToken();
		int roomNumber = Integer.parseInt(st.nextToken());

		cst_buffer.setLength(0);
		cst_buffer.append(YES_SENDWORD);
		cst_buffer.append(SEPARATOR);
		cst_buffer.append(id);
		cst_buffer.append(SEPARATOR);
		cst_buffer.append(cst_roomNumber);
		cst_buffer.append(SEPARATOR);

		try {
			String data = st.nextToken();
			cst_buffer.append(data);

		} catch (NoSuchElementException e) {
		}

		broadcastToClients = new BroadcastToClients(cst_buffer.toString(), roomNumber, cst_waitingRoom, cst_out);
		broadcastToClients.broadcast();
		
	}
}
