package com.ggikko.chatting_command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.ggikko.chatting_process.ChattingProcess;
import com.ggikko.chatting_process.ModifyRoomUser;
import com.ggikko.chatting_process.ModifyWaitUser;
import com.ggikko.chatting_process.SendErrorCode;
import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.SendToClient;
import com.ggikko.chatting_thread.ChattingServerThread;

public class RequestEnterRoom implements ChattingCommand {

	private String receiveData;
	private StringBuffer cst_buffer;
	private WaitingRoom cst_waitingRoom;
	private ChattingServerThread client;
	private int cst_roomNumber;
	private DataOutputStream cst_out;
	
	private SendToClient sendToClient;
	private ChattingProcess modifyWaitUser;
	private ChattingProcess modifyRoomUser;
	private ChattingProcess sendErrorCode;
	
	private static final String SEPARATOR = "|";
	private static final int YES_ENTERROOM = 2021;
	private static final int NO_ENTERROOM = 2022;
	
	public RequestEnterRoom(String receiveData, StringBuffer cst_buffer,
			WaitingRoom cst_waitingRoom, ChattingServerThread client,
			int cst_roomNumber, DataOutputStream cst_out) {
		
		this.receiveData = receiveData;
		this.cst_buffer = cst_buffer;
		this.cst_waitingRoom = cst_waitingRoom;
		this.client = client;
		this.cst_roomNumber = cst_roomNumber;
		this.cst_out = cst_out;
	}
	
	
	@Override
	public void execute() throws IOException {
		
		StringTokenizer st = new StringTokenizer(receiveData, SEPARATOR);
		int command = Integer.parseInt(st.nextToken());
		
		String id, password;
		int roomNumber, result;
		id = st.nextToken();
		roomNumber = Integer.parseInt(st.nextToken());
		try {
			password = st.nextToken();
		} catch (NoSuchElementException e) {
			password = "0";
		}
		result = cst_waitingRoom.joinRoom(id, client, roomNumber,
				password);

		if (result == 0) {
			cst_buffer.setLength(0);
			cst_buffer.append(YES_ENTERROOM);
			cst_buffer.append(SEPARATOR);
			cst_buffer.append(roomNumber);
			cst_buffer.append(SEPARATOR);
			cst_buffer.append(id);
			cst_roomNumber = roomNumber;
			
			
			sendToClient = new SendToClient(cst_buffer.toString(), cst_out);
			sendToClient.send();
			
			modifyRoomUser = new ModifyRoomUser(cst_waitingRoom, roomNumber, id, 1, cst_buffer, cst_out);
			modifyRoomUser.execute();
			
			modifyWaitUser = new ModifyWaitUser(cst_waitingRoom, cst_buffer, cst_out);
			modifyWaitUser.execute();

		} else {
			
			sendErrorCode = new SendErrorCode(NO_ENTERROOM, result, cst_buffer, cst_out);
			sendErrorCode.execute();
		}
	}
}
