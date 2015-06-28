package com.ggikko.chatting_room;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.omg.CORBA.portable.Delegate;

import com.ggikko.chatting_thread.ChattingServerThread;

public class WaitingRoom {

	private static final int MAX_ROOM = 10; 
	private static final int MAX_USER = 100; 
	private static final String SEPARATOR = "|"; 
	private static final String DELIMETER = "'"; 
	private static final String DELIMETER1 = "="; 

	private static final int ERROR_ALREADYUSER = 3001; 
	private static final int ERROR_SERVERFULL = 3002; 
	private static final int ERROR_ROOMSFULL = 3011; 
	private static final int ERROR_ROOMERFULL = 3021; 
	private static final int ERROR_PASSWORD = 3022; 

	private static Vector userVector, roomVector; 
	private static Hashtable userHash, roomHash; 

	private static int userCount; 
	private static int roomCount; 

	static {
		userVector = new Vector(MAX_USER);
		roomVector = new Vector(MAX_ROOM); 
		userHash = new Hashtable(MAX_USER); 
		roomHash = new Hashtable(MAX_ROOM); 
											
		userCount = 0;
		roomCount = 0;
	}

	public synchronized String getRooms() {
		StringBuffer room = new StringBuffer();
		String rooms;
		Integer roomNumber;
		Enumeration enumeration = roomHash.keys();
		while (enumeration.hasMoreElements()) {
			roomNumber = (Integer) enumeration.nextElement();
			ChattingRoom temporaryRoom = (ChattingRoom) roomHash
					.get(roomNumber);
			room.append(String.valueOf(roomNumber));
			room.append(DELIMETER1);
			room.append(temporaryRoom.toString());
			room.append(DELIMETER);
		}
		try {
			rooms = new String(room);
			rooms = rooms.substring(0, rooms.length() - 1);
		} catch (StringIndexOutOfBoundsException e) {
			return "empty";
		}

		return rooms;
	}

	public synchronized String getUsers() {
		StringBuffer id = new StringBuffer();
		String ids;
		Enumeration enumeration = userVector.elements();
		while (enumeration.hasMoreElements()) {
			id.append(enumeration.nextElement());
			id.append(DELIMETER);
		}

		try {
			ids = new String(id);
			ids = ids.substring(0, ids.length() - 1);
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}

		return ids;
	}

	public synchronized Hashtable getClients(int roomNum) {
		if (roomNum == 0)
			return userHash; // 만약 대기방 번호면

		Integer roomNumber = new Integer(roomNum);
		ChattingRoom room = (ChattingRoom) roomHash.get(roomNumber);
		return room.getClients(); 
	}

	public String getWaitRoomInformation() {
		StringBuffer roomInformation = new StringBuffer();
		roomInformation.append(getRooms()); 
		roomInformation.append(SEPARATOR);
		roomInformation.append(getUsers()); 
		return roomInformation.toString();
	}
	
	public String getRoomInfo(int roomNum) {
		Integer roomNumber = new Integer(roomNum);
		ChattingRoom room = (ChattingRoom) roomHash.get(roomNumber);
		return room.getUsers();
	}

	public synchronized int addUser(String id, ChattingServerThread client) {
		if (userCount == MAX_USER)
			return ERROR_SERVERFULL;

		Enumeration ids = userVector.elements();
		while (ids.hasMoreElements()) {
			String temporaryId = (String) ids.nextElement(); 
			if (temporaryId.equals(id))
				return ERROR_ALREADYUSER;
		}
		Enumeration rooms = roomVector.elements();
		while (rooms.hasMoreElements()) {
			ChattingRoom temporaryRoom = (ChattingRoom) rooms.nextElement();
			if (temporaryRoom.checkUserIDs(id))
				return ERROR_ALREADYUSER;
		}

		userVector.addElement(id); 
		userHash.put(id, client);
		client.cst_ID = id;
		client.cst_roomNumber = 0;
		userCount++;

		return 0;
	}

	public synchronized int addRoom(ChattingRoom chattingRoom) {
		if (roomCount == MAX_ROOM)
			return ERROR_ROOMSFULL; 

		roomVector.addElement(chattingRoom); 
		roomHash.put(new Integer(ChattingRoom.roomNumber), chattingRoom); 
		roomCount++;

		return 0;
	}

	public synchronized void delUser(String id) {
		userVector.removeElement(id);
		userHash.remove(id);
		userCount--;
	}
	
	public synchronized int joinRoom(String id, ChattingServerThread client,
			int roomNumber, String password) {

		Integer roomNum = new Integer(roomNumber);
		ChattingRoom room = (ChattingRoom) roomHash.get(roomNum);
		if (room.isRockecd()) { 
			if (room.checkPassword(password)) { 
				if (!room.addUser(id, client)) { 
					return ERROR_ROOMERFULL;
				}
			} else {
				return ERROR_PASSWORD;
			}
		}else if(!room.addUser(id, client)){ 
			return ERROR_ROOMERFULL;
		}

		userVector.removeElement(id);
		userHash.remove(id);
		
		return 0;

	}
	
	public synchronized boolean quitRoom(String id, int roomNumber, ChattingServerThread client){
		boolean returnValue = false;
		Integer roomNum = new Integer(roomNumber);
		ChattingRoom room = (ChattingRoom) roomHash.get(roomNum);
		if(room.delUser(id)){ 
			roomVector.removeElement(room);
			roomHash.remove(roomNum);
			roomCount--;
			returnValue	= true;
		}
		userVector.addElement(id);
		userHash.put(id, client);
		return returnValue;
		
	}
}
