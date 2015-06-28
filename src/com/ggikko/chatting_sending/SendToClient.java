package com.ggikko.chatting_sending;

import java.io.DataOutputStream;
import java.io.IOException;

public class SendToClient {

	private String sendData;
	private DataOutputStream cst_out;

	public SendToClient(String sendData, DataOutputStream cst_out) {
		this.sendData = sendData;
		this.cst_out = cst_out;
	}

	public void send() throws IOException {
		synchronized (cst_out) {
			System.out.println(sendData);
			cst_out.writeUTF(sendData);
			cst_out.flush();
		}
	}
}
