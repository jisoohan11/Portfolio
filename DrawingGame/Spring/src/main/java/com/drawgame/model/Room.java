package com.drawgame.model;

import com.drawgame.config.Config;

import java.util.ArrayList;
import java.util.UUID;
import javax.websocket.Session;

import org.springframework.stereotype.Component;

@Component
public class Room {
	private UUID id = UUID.randomUUID();
	private String name = "";
	private ArrayList<Session> users = new ArrayList<>();
	private int readyCount = 0;
	private int order = 1;
	private Session drawer = null;
	private String corAnswer = "";
	private int correctCount = 1;

	public Room() {

	}

	public void setCorAnswer(String corAnswer) {
		this.corAnswer = corAnswer;
	}

	public String getCorAnswer() {
		return corAnswer;
	}

	public Room(String _name, Session user) {
		name = _name;
		users.add(user);
	}

	public Room(String _name, Session user, UUID _roomUID) {
		name = _name;
		users.add(user);
		id = _roomUID;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID _id) {
		id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String _name) {
		name = _name;
	}

	public int getReadyCount() {
		return readyCount;
	}

	public Session runOrder() {
		int tempOrder = (order++) % users.size();
		drawer = users.get(tempOrder);
		return drawer;
	}

	public Session getDrawer() {
		return drawer;
	}

	public Session setDrawer(Session user) {
		if (isIn(user)) {
			drawer = user;
		}
		return drawer;
	}

	public int getCorrectCount() {
		return correctCount;
	}

	public void initCorrectCount() {
		correctCount = 1;
	}

	public boolean incCorrectCount() {
		if (this.correctCount >= users.size()) {
			return false;
		}

		this.correctCount++;
		return true;
	}

	public boolean decCorrectCount() {
		if (this.correctCount <= 1) {
			return false;
		}

		this.correctCount--;
		return true;
	}

	public boolean gameFinished() {
		return (correctCount > 1) && (correctCount == users.size());
	}

	public void initReadyCount() {
		readyCount = 0;
	}

	public boolean readyToStart() {
		return (readyCount > 1) && (readyCount == users.size());
	}

	public boolean incReadyCount() {
		if (this.readyCount >= users.size()) {
			return false;
		}

		this.readyCount++;
		return true;
	}

	public boolean decReadyCount() {
		if (this.readyCount <= 0) {
			return false;
		}

		this.readyCount--;
		return true;
	}

	public ArrayList<Session> getUserSessions() {
		return users;
	}

	public ArrayList<User> getUsers() {
		ArrayList<User> list = new ArrayList<>();
		for (Session member : users) {
			list.add(new User(Integer.parseInt(member.getUserProperties().get(Config.SESS_USER_ID).toString()),
					member.getUserProperties().get(Config.SESS_USER_NAME).toString()));
		}

		return list;
	}

	public ArrayList<User> getAnotherUsers(final String id) {
		ArrayList<User> list = new ArrayList<>();
		for (Session member : users) {
			if (member.getUserProperties().get(Config.SESS_USER_ID).toString().equals(id))
				continue;

			list.add(new User(Integer.parseInt(member.getUserProperties().get(Config.SESS_USER_ID).toString()),
					member.getUserProperties().get(Config.SESS_USER_NAME).toString()));
		}

		return list;
	}

	public void enter(final Session user) {
		users.add(user);
	}

	public void leave(final Session user) {
		users.remove(user);
	}

	public int userCount() {
		return users.size();
	}

	public boolean isIn(final Session user) {
		for (Session member : users) {
			if (member.getId() != user.getId())
				continue;

			return true;
		}

		return false;
	}

	public boolean isEmpty() {
		return users.isEmpty();
	}
}
