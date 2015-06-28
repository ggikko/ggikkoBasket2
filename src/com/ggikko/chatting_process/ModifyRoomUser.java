package com.ggikko.chatting_process;

import java.io.DataOutputStream;
import java.io.IOException;
import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.BroadcastToClients;

public class ModifyRoomUser implements ChattingProcess {

	private WaitingRoom cst_waitingRoom;
	private int roomNumber;
	private String id;
	private int code;
	private StringBuffer cst_buffer;
	private DataOutputStream cst_out;
	private BroadcastToClients broadcastToClients;
	
	private static final int MODIFY_ROOMUSER = 2013;
	private static final String SEPARATOR = "|";
	
	public ModifyRoomUser(WaitingRoom cst_waitingRoom, int roomNumber,
			String id, int code, StringBuffer cst_buffer, DataOutputStream cst_out) {
	
		this.cst_waitingRoom = cst_waitingRoom;
		this.roomNumber = roomNumber;
		this.id = id;
		this.code = code;
		this.cst_buffer = cst_buffer;
		this.cst_out = cst_out;
	}
	
	@Override
	public void execute() throws IOException {		
		String ids = cst_waitingRoom.getRoomInfo(roomNumber);
		cst_buffer.setLength(0);
		cst_buffer.append(MODIFY_ROOMUSER);
		cst_buffer.append(SEPARATOR);
		cst_buffer.append(id);
		cst_buffer.append(SEPARATOR);
		cst_buffer.append(code);
		cst_buffer.append(SEPARATOR);
		cst_buffer.append(ids);
		
		broadcastToClients = new BroadcastToClients(cst_buffer.toString(), roomNumber, cst_waitingRoom, cst_out);
		broadcastToClients.broadcast();
	}
}
