package com.ggikko.chatting_process;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ggikko.chatting_room.WaitingRoom;
import com.ggikko.chatting_sending.BroadcastToClients;

public class ModifyWaitingRoom implements ChattingProcess {

	private static final int WAITINGROOM = 0;
	private WaitingRoom cst_waitingRoom;
	private StringBuffer cst_buffer;
	private DataOutputStream cst_out;

	private BroadcastToClients broadcastToClients;

	private static final int MODIFY_WAITINFORMATION = 2013;
	private static final String SEPARATOR = "|";

	public ModifyWaitingRoom(WaitingRoom cst_waitingRoom,
			StringBuffer cst_buffer, DataOutputStream cst_out) {

		this.cst_waitingRoom = cst_waitingRoom;
		this.cst_buffer = cst_buffer;
		this.cst_out = cst_out;
	}

	@Override
	public void execute() throws IOException {

		cst_buffer.setLength(0);
		cst_buffer.append(MODIFY_WAITINFORMATION);
		cst_buffer.append(SEPARATOR);
		cst_buffer.append(cst_waitingRoom.getWaitRoomInformation());

		broadcastToClients = new BroadcastToClients(cst_buffer.toString(),
				WAITINGROOM, cst_waitingRoom, cst_out);
		broadcastToClients.broadcast();
	}
}
