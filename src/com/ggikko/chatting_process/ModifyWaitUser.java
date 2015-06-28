package com.ggikko.chatting_process;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.BroadcastToClients;

public class ModifyWaitUser implements ChattingProcess {

	private WaitingRoom cst_waitingRoom;
	private StringBuffer cst_buffer;
	private DataOutputStream cst_out;
	
	private BroadcastToClients broadcastToClients;
	
	private static final int MODIFY_WAITUSER = 2013;
	private static final String SEPARATOR = "|";
	private static final int WAITINGROOM = 0;
	
	public ModifyWaitUser(WaitingRoom cst_waitingRoom,
			StringBuffer cst_buffer, DataOutputStream cst_out) {
		super();
		this.cst_waitingRoom = cst_waitingRoom;
		this.cst_buffer = cst_buffer;
		this.cst_out = cst_out;
	}

	@Override
	public void execute() throws IOException {
		String ids = cst_waitingRoom.getUsers();
		cst_buffer.setLength(0);
		cst_buffer.append(MODIFY_WAITUSER);
		cst_buffer.append(SEPARATOR);
		cst_buffer.append(ids);
		
		broadcastToClients = new BroadcastToClients(cst_buffer.toString(), WAITINGROOM, cst_waitingRoom, cst_out);
		broadcastToClients.broadcast();	
	}
}
