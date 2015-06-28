package com.ggikko.chatting_command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.ggikko.chatting_process.ChattingProcess;
import com.ggikko.chatting_process.ModifyWaitUser;
import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.SendToClient;

public class RequestLogout implements ChattingCommand {
	
	private String receiveData;
	private StringBuffer cst_buffer;
	private WaitingRoom cst_waitingRoom;
	private DataOutputStream cst_out;
	
	private SendToClient sendToClient;
	private ChattingProcess modifyWaitUser;

	private static final String SEPARATOR = "|";
	private static final int YES_LOGOUT = 2041;
	
	public RequestLogout(String receiveData, StringBuffer cst_buffer,
			WaitingRoom cst_waitingRoom, DataOutputStream cst_out) {
		this.receiveData = receiveData;
		this.cst_buffer = cst_buffer;
		this.cst_waitingRoom = cst_waitingRoom;
		this.cst_out = cst_out;
	}

	@Override
	public void execute() throws IOException {
		
		StringTokenizer st = new StringTokenizer(receiveData, SEPARATOR);
		int command = Integer.parseInt(st.nextToken());
		
		String id = st.nextToken();
		cst_waitingRoom.delUser(id);

		cst_buffer.setLength(0);
		cst_buffer.append(YES_LOGOUT);
		
		sendToClient = new SendToClient(cst_buffer.toString(), cst_out);
		sendToClient.send();
		
		modifyWaitUser = new ModifyWaitUser(cst_waitingRoom, cst_buffer, cst_out);
		modifyWaitUser.execute();
	}
}
