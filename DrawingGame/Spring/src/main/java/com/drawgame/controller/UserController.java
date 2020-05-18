package com.drawgame.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.drawgame.model.User;
import com.drawgame.repository.UserRepository;

@Component
@RestController
public class UserController {

	@Autowired
	UserRepository db;

	@GetMapping("/user/{id}")
	User getPerson(@PathVariable Integer Id_number) {
		return db.getOne(Id_number);
	}

	@GetMapping("/user/{password}")
	User getpass(@PathVariable Integer passward) {
		return db.getOne(passward);
	}

	@GetMapping("/user/{friend}")
	User getFirend(@PathVariable Integer friend) {
		return db.getOne(friend);
	}

	@RequestMapping("/users")
	List<User> getUsers() {
		return db.findAll();
	}

	User addUser(String username, String password) {
		User p = new User();
		p.setUser(username);
		p.setpass(password);
		db.save(p);
		return p;
	}

	@GetMapping("/deleteAll")
	String deleteAllUsers() {
		db.deleteAll();
		return "All users have been deleted";
	}

	@GetMapping("/deleteuser/{id}")
	String deleteUser(@PathVariable Integer id) {
		String user = db.findById(id).get().getUser();
		db.deleteById(id);
		return user + " has been deleted";
	}

	@GetMapping("/addfriend/{id}/{fname}")
	String addFriend(@PathVariable Integer id, @PathVariable String fname) {
		User user = db.findById(id).get();
		Integer fid = findIdbyusername(fname);
		if (fid == -1) {
			return "Could not find friend username: " + fname;
		}
		user.addFriend(fid, fname);
		db.save(user);
		return fname + " has been added";
	}

	@GetMapping("/delfriend/{id}/{fid}")
	String deleteFriend(@PathVariable Integer id, @PathVariable Integer fid) {
		User user = db.findById(id).get();
		user.deleteFriend(fid);
		db.save(user);
		return " your friend has been deleted";
	}

	@RequestMapping("/getfriend/{id}")
	JSONObject getFriends(@PathVariable Integer id) {
		JSONObject friends = db.findById(id).get().getFriend();
		System.out.print(friends.toJSONString());
		return friends;
	}

	@GetMapping("/editpassword/{id}/{newpass}")
	String changePass(@PathVariable Integer id, @PathVariable String newpass) {
		User user = db.findById(id).get();
		user.setpass(newpass);
		db.save(user);
		return "password has been changed";
	}
	
	@GetMapping("/editScore/{id}/{newscore}")
	String changeScore(@PathVariable Integer id, @PathVariable int newscore) {
		User user = db.findById(id).get();
		user.setScore(newscore);
		db.save(user);
		return "score has been changed";
	}

	Integer findIdbyusername(String username) {
		Integer result = -1;
		List<User> list = db.findAll();
		Iterator<User> iterator = list.iterator();
		while (iterator.hasNext()) {
			User currentUser = iterator.next();
			String currentUsername = currentUser.getUser();
			if (currentUsername.equals(username)) {
				result = currentUser.getId();
				return result;
			}
		}

		return result;
	}

	String findNamebyId(int id) {
		String result = "";
		List<User> list = db.findAll();
		Iterator<User> iterator = list.iterator();
		while (iterator.hasNext()) {
			User currentUser = iterator.next();
			int currentId = currentUser.getId();
			if (currentId == id) {
				result = currentUser.getUser();
				return result;
			}
		}

		return result;
	}

	@GetMapping("/findid/{username}")
	String findUsername(@PathVariable String username) {
		Integer result = findIdbyusername(username);
		if (result != -1) {
			return username + " ID is: " + result;
		}
		return "user not found";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/json.all")
	public @ResponseBody JSONObject jsonAll() {
		JSONObject jsonMain = new JSONObject();
		JSONArray jArray = new JSONArray();
		JSONObject row;
		List<User> list = db.findAll();
		for (int i = 0; i < list.size(); i++) {
			row = new JSONObject();
			row.put("id", list.get(i).getId());
			row.put("user", list.get(i).getUser());
			row.put("pass", list.get(i).getpass());
			row.put("friend", list.get(i).getFriend());
			row.put("email", list.get(i).getEmail());
			jArray.add(i, row);

			jsonMain.put("members", jArray);
		}
		return jsonMain;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/potentialFriends/{ID}")
	public @ResponseBody JSONObject potentialFriends(@PathVariable Integer ID) {
		JSONObject jsonMain = new JSONObject();
		JSONArray jArray = new JSONArray();
		JSONObject row;
		List<User> list = db.findAll();
		User currentUser = list.get(ID - 1);
		JSONObject currentFriends = currentUser.getFriend();
		List<Integer> FriendsIDs = new ArrayList<Integer>();

		for (Iterator iterator = currentFriends.keySet().iterator(); iterator.hasNext();) {
			Integer key = (Integer) iterator.next();
			FriendsIDs.add(key);
		}

		for (int i = 0; i < list.size(); i++) {
			Integer curr = list.get(i).getId();
			if (FriendsIDs.contains(curr)) {
				list.remove(i);
				i--;
			}
		}

		list.remove(ID - 1); // remove yourself form the the list (givenID)
		for (int i = 0; i < list.size(); i++) {

			row = new JSONObject();
			row.put("id", list.get(i).getId());
			row.put("user", list.get(i).getUser());
			row.put("pass", list.get(i).getpass());
			row.put("friend", list.get(i).getFriend());
			row.put("email", list.get(i).getEmail());
			jArray.add(i, row);

			jsonMain.put("members", jArray);
		}
		return jsonMain;
	}

}
