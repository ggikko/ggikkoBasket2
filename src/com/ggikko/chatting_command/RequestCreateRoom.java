package com.ggikko.chatting_command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.ggikko.chatting_process.ChattingProcess;
import com.ggikko.chatting_process.ModifyRoomUser;
import com.ggikko.chatting_process.ModifyWaitingRoom;
import com.ggikko.chatting_process.SendErrorCode;
import com.ggikko.chatting_room.ChattingRoom;
import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.SendToClient;
import com.ggikko.chatting_thread.ChattingServerThread;


public class RequestCreateRoom implements ChattingCommand {
	
	private String receiveData;
	private WaitingRoom cst_waitingRoom;
	private int cst_roomNumber;
	private StringBuffer cst_buffer;
	private String cst_ID;
	private DataOutputStream cst_out;
	private ChattingServerThread client;
	
	private SendToClient sendToClient;
	private ChattingProcess modifyWaitingRoom;
	private ChattingProcess modifyRoomUser;
	private ChattingProcess sendErrorCode;
	
	private static final int NO_CREATEROOM = 2012;
	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "'";
	private static final int YES_CREATEROOM = 2011;

	public RequestCreateRoom(String receiveData, WaitingRoom cst_waitingRoom,
			int cst_roomNumber, StringBuffer cst_buffer, String cst_ID,
			DataOutputStream cst_out, ChattingServerThread client) {
		
		this.receiveData = receiveData;
		this.cst_waitingRoom = cst_waitingRoom;
		this.cst_roomNumber = cst_roomNumber;
		this.cst_buffer = cst_buffer;
		this.cst_ID = cst_ID;
		this.cst_out = cst_out;
		this.client = client;
	}

	@Override
	public void execute() throws IOException  {
		
		
		StringTokenizer st = new StringTokenizer(receiveData, SEPARATOR);
		int command = Integer.parseInt(st.nextToken());
		
		String id, roomName, password;
		int roomMaxUser, result;
		boolean isRock;
		id = st.nextToken();
		String roomInfomation = st.nextToken();
		StringTokenizer room = new StringTokenizer(roomInfomation,
				DELIMETER);
		roomName = room.nextToken();
		roomMaxUser = Integer.parseInt(room.nextToken());
		isRock = (Integer.parseInt(room.nextToken()) == 0) ? false
				: true;
		password = room.nextToken();

		ChattingRoom chattingRoom = new ChattingRoom(roomName,
				roomMaxUser, isRock, password, id);

		result = cst_waitingRoom.addRoom(chattingRoom);

		if (result == 0) {
			cst_roomNumber = ChattingRoom.getRoomNumber();
			boolean temporary = chattingRoom.addUser(cst_ID, client);
			cst_waitingRoom.delUser(cst_ID);

			cst_buffer.setLength(0);
			cst_buffer.append(YES_CREATEROOM);
			cst_buffer.append(SEPARATOR);
			cst_buffer.append(cst_roomNumber);
			
			sendToClient = new SendToClient(cst_buffer.toString(), cst_out);
			sendToClient.send();
			
			modifyWaitingRoom = new ModifyWaitingRoom(cst_waitingRoom, cst_buffer, cst_out);
			modifyWaitingRoom.execute();
			
			modifyRoomUser = new ModifyRoomUser(cst_waitingRoom, cst_roomNumber, id, 1, cst_buffer, cst_out);
			modifyRoomUser.execute();
		} else {
			
			sendErrorCode = new SendErrorCode(NO_CREATEROOM, result, cst_buffer, cst_out);
			sendErrorCode.execute();
		}
		
	}

}
