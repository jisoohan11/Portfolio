package com.drawgame.controller;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drawgame.model.User;
import com.drawgame.repository.UserRepository;

@Component
@RestController
public class RegisterController {

	@Autowired
	UserRepository db;

	// If it returns 1 the user registered successfully
	// if it returns -1 if the username already exits
	@GetMapping("/register/{username}/{password}")
	public String userRegistration(@PathVariable String username, @PathVariable String password) {
		Integer userID = findIdbyusername(username);

		// adds the user with the given username & passwrord if the username does not
		// already exist
		if (userID == -1) {
			User p = new User();
			p.setUser(username);
			p.setpass(password);
			db.save(p);
			return "1";
		} else {
			return "-1"; // username exists
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

	// for testing and quick initializing for userbase
	@GetMapping("/initub")
	private void initub() {

		userRegistration("ahmed", "123");
		userRegistration("kayle", "123");
		userRegistration("joo", "123");
		userRegistration("jisoo", "123");
		userRegistration("x", "123");
		userRegistration("y", "123");

	}

}
