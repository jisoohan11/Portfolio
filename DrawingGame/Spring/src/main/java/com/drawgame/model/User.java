package com.drawgame.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component
@Entity
public class User implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer Id;

	@Column // (updatable = false, nullable = false)
	private String username;

	@Column
	private String password;

	@Column
	private JSONObject friend = new JSONObject();

	@Column
	private String email;
	
	@Column
	private int score;

	public User(Integer Id, String username) {
		this.Id = Id;
		this.username = username;
	}

	public User() {
	}

	public Integer getId() {
		return Id;
	}

	public String getUser() {
		return username;
	}

	public String getpass() {
		return password;
	}
	
	public int getScore() {
		return score;
	}

	public JSONObject getFriend() {
		return friend;
	}

	@SuppressWarnings("unchecked")
	public void addFriend(Integer fid, String fname) {
		friend.put(fid, fname);
	}

	public void deleteFriend(Integer fid) {
		friend.remove(fid);
	}

	public String getEmail() {
		return email;
	}

	public void setID(Integer id) {
		this.Id = id;
	}

	public void setUser(String username) {
		this.username = username;
	}

	public void setpass(String password) {
		this.password = password;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
}
