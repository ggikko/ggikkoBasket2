package com.ggikko.chatting_process;

import java.io.DataOutputStream;
import java.io.IOException;

import com.ggikko.chatting_sending.SendToClient;

public class SendErrorCode implements ChattingProcess {
	private int errorCode;
	private StringBuffer cst_buffer;
	private DataOutputStream cst_out; 
	private SendToClient sendToClient;
	private static final String SEPARATOR = "|";
	
	private int message;
	public SendErrorCode(int message, int errorCode,
			StringBuffer cst_buffer, DataOutputStream cst_out) {
		super();
		this.message = message;
		this.errorCode = errorCode;
		this.cst_buffer = cst_buffer;
		this.cst_out = cst_out;
	}
	
	@Override
	public void execute() throws IOException {
		cst_buffer.append(message);
		cst_buffer.append(SEPARATOR);
		cst_buffer.append(errorCode);
		sendToClient = new SendToClient(cst_buffer.toString(), cst_out);
		sendToClient.send();
	}
}
