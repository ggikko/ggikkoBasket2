package com.ggikko.chatting_thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;










import com.ggikko.chatting_command.ChattingCommand;
import com.ggikko.chatting_command.RequestCoerceOut;
import com.ggikko.chatting_command.RequestCreateRoom;
import com.ggikko.chatting_command.RequestEnterRoom;
import com.ggikko.chatting_command.RequestLogon;
import com.ggikko.chatting_command.RequestLogout;
import com.ggikko.chatting_command.RequestQuitRoom;
import com.ggikko.chatting_command.RequestSendWord;
import com.ggikko.chatting_room.WaitingRoom;

public class ChattingServerThread extends Thread{
	
	private Socket cst_socket;
	private DataInputStream cst_in;
	private DataOutputStream cst_out;
	private StringBuffer cst_buffer;
	private WaitingRoom cst_waitingRoom;
	private ChattingCommand requestCoerceOut;
	private ChattingCommand requestCreateRoom;
	private ChattingCommand requestEnterRoom;
	private ChattingCommand requestLogon;
	private ChattingCommand requestLogout;
	private ChattingCommand requestQuitRoom;
	private ChattingCommand requestSendWord;
	
	public String cst_ID;
	public int cst_roomNumber;
	
	private static final String SEPARATOR = "|";
	
	private static final int REQUEST_LOGON = 1001;
	private static final int REQUEST_CRATEROOM = 1011;
	private static final int REQUEST_ENTERROOM = 1021;
	private static final int REQUEST_QUITROOM = 1031;
	private static final int REQUEST_LOGOUT = 1041;
	private static final int REQUEST_SENDWORD = 1051;
	private static final int REQUEST_COERCEOUT = 1053;


	public ChattingServerThread(Socket socket) {
		try {
			cst_socket = socket;
			cst_in = new DataInputStream(cst_socket.getInputStream());
			cst_out = new DataOutputStream(cst_socket.getOutputStream());
			cst_buffer = new StringBuffer(2048); 
			cst_waitingRoom = new WaitingRoom();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void run() {
		try {
			while (true) {
				String receiveData = cst_in.readUTF();
				System.out.println(receiveData);

				StringTokenizer st = new StringTokenizer(receiveData, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {

				case REQUEST_LOGON: {				
					requestLogon = new RequestLogon(cst_roomNumber, cst_ID, receiveData, cst_waitingRoom, this, cst_buffer, cst_out);
					requestLogon.execute();
					break;
				}

				case REQUEST_CRATEROOM: {
					requestCreateRoom = new RequestCreateRoom(receiveData, cst_waitingRoom, cst_roomNumber, cst_buffer, cst_ID, cst_out, this);
					requestCreateRoom.execute();
					break;
				}

				case REQUEST_ENTERROOM: {
					requestEnterRoom = new RequestEnterRoom(receiveData, cst_buffer, cst_waitingRoom, this, cst_roomNumber, cst_out);
					requestEnterRoom.execute();
					break;
				}

				case REQUEST_QUITROOM: {			
					requestQuitRoom = new RequestQuitRoom(receiveData, cst_buffer, cst_waitingRoom, cst_roomNumber, this, cst_out);
					requestQuitRoom.execute();
					break;
				}

				case REQUEST_LOGOUT: {
					requestLogout = new RequestLogout(receiveData, cst_buffer, cst_waitingRoom, cst_out);
					requestLogout.execute();
					release();
					break;
				}

				case REQUEST_SENDWORD: {
					requestSendWord = new RequestSendWord(receiveData, cst_buffer, cst_roomNumber, cst_waitingRoom, cst_out);
					requestSendWord.execute();
					break;
				}

				case REQUEST_COERCEOUT: {
					requestCoerceOut = new RequestCoerceOut(receiveData, cst_waitingRoom, cst_buffer, cst_out);
					requestCoerceOut.execute();
					
					break;
				}

				}
				Thread.sleep(100);
			}

		} catch (NullPointerException e) {
		} catch (InterruptedException e) {
			System.out.println(e);

			if (cst_roomNumber == 0) {
				cst_waitingRoom.delUser(cst_ID);
			} else {
				boolean temporary = cst_waitingRoom.quitRoom(cst_ID,
						cst_roomNumber, this);
				cst_waitingRoom.delUser(cst_ID);
			}
			release();

		} catch (IOException e) {
			System.out.println(e);

			if (cst_roomNumber == 0) {
				cst_waitingRoom.delUser(cst_ID);
			} else {
				boolean temporary = cst_waitingRoom.quitRoom(cst_ID,
						cst_roomNumber, this);
				cst_waitingRoom.delUser(cst_ID);
			}
			release();
		}
	}
	
	
	public void release() {
		try {
			if (cst_in != null)
				cst_in.close();

		} catch (IOException e1) {
			System.out.println("release error");
		} finally {
			cst_in = null;
		}
		try {
			if (cst_out != null)
				cst_out.close();
		} catch (IOException e1) {
			System.out.println("release error");
		} finally {
			cst_out = null;
		}
		try {
			if (cst_socket != null)
				cst_socket.close();
		} catch (IOException e1) {
			System.out.println("release error");
		} finally {
			cst_socket = null;
		}
		if (cst_ID != null) {
			System.out.println(cst_ID + "연결 종료");
			cst_ID = null;
		}
	}
	
}
