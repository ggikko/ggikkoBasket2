package com.ggikko.chatting_command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.ggikko.chatting_process.ChattingProcess;
import com.ggikko.chatting_process.ModifyRoomUser;
import com.ggikko.chatting_process.ModifyWaitingRoom;
import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.SendToClient;
import com.ggikko.chatting_thread.ChattingServerThread;

public class RequestCoerceOut implements ChattingCommand {
	
	private String receiveData;
	private WaitingRoom cst_waitingRoom;
	private StringBuffer cst_buffer;
	private DataOutputStream cst_out;
	private ChattingProcess modifyWaitingRoom;
	private ChattingProcess modifyRoomUser;

	private SendToClient sendToClient;
	
	private static final String SEPARATOR = "|";
	private static final int YES_COERCEOUT = 2054;
	
	public RequestCoerceOut(String receiveData, WaitingRoom cst_waitingRoom,
			StringBuffer cst_buffer, DataOutputStream cst_out) {
		
		this.receiveData = receiveData;
		this.cst_waitingRoom = cst_waitingRoom;
		this.cst_buffer = cst_buffer;
		this.cst_out = cst_out;
	}
	
	@Override
	public void execute() throws IOException {
		
		StringTokenizer st = new StringTokenizer(receiveData, SEPARATOR);
		int command = Integer.parseInt(st.nextToken());
		
		int roomNumber = Integer.parseInt(st.nextToken());
		String idTo = st.nextToken();
		boolean updateWaitInformation;
		Hashtable room = cst_waitingRoom.getClients(roomNumber);
		ChattingServerThread client = null;
		client = (ChattingServerThread) room.get(idTo);
		updateWaitInformation = cst_waitingRoom.quitRoom(idTo,
				roomNumber, client);

		cst_buffer.setLength(0);
		cst_buffer.append(YES_COERCEOUT);
		
		sendToClient = new SendToClient(cst_buffer.toString(), cst_out);
		sendToClient.send();
		
		client.cst_roomNumber = 0;

		if (updateWaitInformation) {
			
			modifyWaitingRoom = new ModifyWaitingRoom(cst_waitingRoom, cst_buffer, cst_out);
			modifyWaitingRoom.execute();
			
		} else {
			modifyWaitingRoom = new ModifyWaitingRoom(cst_waitingRoom, cst_buffer, cst_out);
			modifyWaitingRoom.execute();
			modifyRoomUser = new ModifyRoomUser(cst_waitingRoom, roomNumber, idTo, 2, cst_buffer, cst_out);
			modifyRoomUser.execute();
		}
		
	}

}
