package com.ggikko.chatting_command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.ggikko.chatting_process.ChattingProcess;
import com.ggikko.chatting_process.ModifyWaitUser;
import com.ggikko.chatting_process.SendErrorCode;
import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.SendToClient;
import com.ggikko.chatting_thread.ChattingServerThread;

public class RequestLogon implements ChattingCommand {

	private int cst_roomNumber;
	private String cst_ID;
	private String receiveData;
	private WaitingRoom cst_waitingRoom;
	private ChattingServerThread client;
	private StringBuffer cst_buffer;

	private DataOutputStream cst_out;
	
	private SendToClient sendToClient;
	private ChattingProcess modifyWaitUser;
	private ChattingProcess sendErrorCode;
	
	private static final String SEPARATOR = "|";
	private static final int WAITINGROOM = 0;
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	
	public RequestLogon(int cst_roomNumber, String cst_ID, String receiveData,
			WaitingRoom cst_waitingRoom, ChattingServerThread client,
			StringBuffer cst_buffer, DataOutputStream cst_out) {
		
		this.cst_roomNumber = cst_roomNumber;
		this.cst_ID = cst_ID;
		this.receiveData = receiveData;
		this.cst_waitingRoom = cst_waitingRoom;
		this.client = client;
		this.cst_buffer = cst_buffer;
		this.cst_out = cst_out;
	}
	
	@Override
	public void execute() throws IOException {
		
		StringTokenizer st = new StringTokenizer(receiveData, SEPARATOR);
		int command = Integer.parseInt(st.nextToken());
		
		cst_roomNumber = WAITINGROOM;
		int result;
		cst_ID = st.nextToken();
		result = cst_waitingRoom.addUser(cst_ID, client);

		cst_buffer.setLength(0);

		if (result == 0) {
			cst_buffer.append(YES_LOGON);
			cst_buffer.append(SEPARATOR);
			cst_buffer.append(cst_waitingRoom.getRooms());
			
			sendToClient = new SendToClient(cst_buffer.toString(), cst_out);
			sendToClient.send();
			
			modifyWaitUser = new ModifyWaitUser(cst_waitingRoom, cst_buffer, cst_out);
			modifyWaitUser.execute();
			
			System.out.println(cst_ID + "connecting");
		} else {
			sendErrorCode = new SendErrorCode(NO_LOGON, result, cst_buffer, cst_out);
			sendErrorCode.execute();
		}
	}
}
