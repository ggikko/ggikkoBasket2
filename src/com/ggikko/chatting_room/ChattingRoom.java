package com.ggikko.chatting_room;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.ggikko.chatting_thread.ChattingServerThread;

public class ChattingRoom {

	public static int roomNumber = 0;

	private static final String DELIMETER = "'";
	private static final String DELIMETER1 = "=";

	private Vector userVector;
	private Hashtable userHash;
	private String roomName;
	private int roomMaxUser;
	private int roomUser;
	private boolean isRock;
	private String password;
	private String admin;

	public ChattingRoom(String roomName, int roomMaxUser, boolean isRock,
			String password, String admin) {
		roomNumber++;
		this.roomName = roomName;
		this.roomUser = roomMaxUser;
		this.isRock = isRock;
		this.password = password;
		this.admin = admin;
		this.userVector = new Vector(roomMaxUser);
		this.userHash = new Hashtable(roomMaxUser);

	}

	public Hashtable getClients() {
		return userHash;
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

	public boolean checkUserIDs(String id) {
		Enumeration ids = userVector.elements();
		while (ids.hasMoreElements()) {
			String temporaryId = (String) ids.nextElement();
			if (temporaryId.equals(id))
				return true;
		}
		return false;
	}

	public static synchronized int getRoomNumber() {
		return roomNumber;
	}

	public boolean addUser(String id, ChattingServerThread client) {
		if (roomUser == roomMaxUser) {
			return false;
		}

		userVector.addElement(id);
		userHash.put(id, client);
		roomUser++;
		return true;
	}

	public boolean isRockecd() {
		return isRock;
	}

	public boolean checkPassword(String passwd) {
		return password.equals(passwd);
	}

	public boolean delUser(String id) {
		userVector.removeElement(id);
		userHash.remove(id);
		roomUser--;
		return userVector.isEmpty();
	}

}
