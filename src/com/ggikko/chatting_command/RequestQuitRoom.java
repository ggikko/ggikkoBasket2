package com.ggikko.chatting_command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.ggikko.chatting_process.ChattingProcess;
import com.ggikko.chatting_process.ModifyRoomUser;
import com.ggikko.chatting_process.ModifyWaitingRoom;
import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.SendToClient;
import com.ggikko.chatting_thread.ChattingServerThread;

public class RequestQuitRoom implements ChattingCommand {

	private String receiveData;
	private StringBuffer cst_buffer;
	private WaitingRoom cst_waitingRoom;
	private int cst_roomNumber;
	private ChattingServerThread client;
	private DataOutputStream cst_out;
	
	private SendToClient sendToClient;
	private ChattingProcess modifyWaitingRoom;
	private ChattingProcess modifyRoomUser;

	private static final String SEPARATOR = "|";
	private static final int YES_QUITROOM = 2031;
	private static final int WAITINGROOM = 0;
	
	public RequestQuitRoom(String receiveData, StringBuffer cst_buffer,
			WaitingRoom cst_waitingRoom, int cst_roomNumber,
			ChattingServerThread client, DataOutputStream cst_out) {
		this.receiveData = receiveData;
		this.cst_buffer = cst_buffer;
		this.cst_waitingRoom = cst_waitingRoom;
		this.cst_roomNumber = cst_roomNumber;
		this.client = client;
		this.cst_out = cst_out;
	}
	
	@Override
	public void execute() throws IOException {
		
		StringTokenizer st = new StringTokenizer(receiveData, SEPARATOR);
		int command = Integer.parseInt(st.nextToken());
		
		String id;
		int roomNumber;
		boolean updateWaitInformation;
		id = st.nextToken();
		roomNumber = Integer.parseInt(st.nextToken());

		updateWaitInformation = cst_waitingRoom.quitRoom(id,
				roomNumber, client);

		cst_buffer.setLength(0);
		cst_buffer.append(YES_QUITROOM);
		cst_buffer.append(SEPARATOR);
		cst_buffer.append(id);
		
		sendToClient = new SendToClient(cst_buffer.toString(), cst_out);
		sendToClient.toString();
		
		cst_roomNumber = WAITINGROOM;

		if (updateWaitInformation) {
			
			modifyWaitingRoom = new ModifyWaitingRoom(cst_waitingRoom, cst_buffer, cst_out);
			modifyWaitingRoom.execute();
			
		} else {
			modifyWaitingRoom = new ModifyWaitingRoom(cst_waitingRoom, cst_buffer, cst_out);
			modifyWaitingRoom.execute();
			modifyRoomUser = new ModifyRoomUser(cst_waitingRoom, roomNumber, id, 0, cst_buffer, cst_out);
			
		}
		
	}

}
