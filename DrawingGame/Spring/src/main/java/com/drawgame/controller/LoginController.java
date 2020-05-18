package com.drawgame.controller;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.drawgame.model.User;
import com.drawgame.repository.UserRepository;

@Component
@RestController
public class LoginController {

	@Autowired
	UserRepository db;

	// If it returns an ID number ( num > 0) then it was successful
	// if it returns 0 then the password is incorrect
	// if it returns -1 then the username does not exist
	@GetMapping("/login/{username}/{password}")
	public String userLogin(@PathVariable String username, @PathVariable String password) {
		Integer userID = findIdbyusername(username);

		if (userID == -1) {
			return "-1"; // user not found
		}

		// otherwise check if password is correct
		if (verifyPassword(userID, password)) {
			return "" + userID; // login successful
		} else {
			return "0"; // user found but password is incorrect
		}
	}

	public Integer findIdbyusername(String username) {
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

	public boolean verifyPassword(Integer userID, String password) {

		User user = db.findById(userID).get();
		if (user.getpass().equals(password)) {
			return true;
		} else {
			return false;
		}

	}

}
