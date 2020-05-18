package drawgame;

import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class user {
	

	@Autowired
	database db;
	
	@GetMapping("/person/{id}")
	Person getPerson(@PathVariable Integer Id_number) 
	{
		return db.getOne(Id_number);
	}
	
	@GetMapping("/person/{password}")
	Person getpass(@PathVariable Integer passward)
	{
		return db.getOne(passward);
	}
	@GetMapping("/person/{friend}")
	Person getFirend(@PathVariable Integer friend)
		{
			return db.getOne(friend);
		}
		
	@RequestMapping("/persons")
	List<Person> hello() {
		return db.findAll();
	}
	
	@PostMapping("/person")
	Person createPerson(@RequestBody Person p) {
		db.save(p);
		return p;
	}
	
	@GetMapping("/newp/{username}/{password}")
	String newUser(@PathVariable String username, @PathVariable String password){
		Person p = new Person();
		p.username = username;
		p.password = password;
		db.save(p);
		return username + " has been added";
	}
	
	@GetMapping("/deleteAll")
	String deleteAllUsers(){
		db.deleteAll();
		return "All users have been deleted";
	}
	
	@GetMapping("/delp/{id}")
	String deleteUser(@PathVariable Integer id){
		String user = db.findById(id).get().username;
		db.deleteById(id);
		return user + " has been deleted";
	}
	
	@GetMapping("/addf/{id}/{fname}")
	String addFriend(@PathVariable Integer id, @PathVariable String fname){
		Person user = db.findById(id).get();
		Integer fid = findIdbyusername(fname);
		if (fid == -1) {
			return "Could not find friend username: " + fname;
		}
		user.addFriend(fid, fname);
		db.save(user);
		return fname + " has been added";
	}
	
	@GetMapping("/delf/{id}/{fid}")
	String deleteFriend(@PathVariable Integer id, @PathVariable Integer fid){
		Person user = db.findById(id).get();
		user.deleteFriend(fid);
		db.save(user);
		return " your friend has been deleted";
	}
	
	@GetMapping("/delfn/{id}/{fname}")
    String deleteFriendName(@PathVariable Integer id, @PathVariable String fname){
        Person user = db.findById(id).get();
        Integer fid = findIdbyusername(fname);
        user.deleteFriend(fid);
        db.save(user);
        return " your friend has been deleted";
    }
	
	@RequestMapping("/getf/{id}")
	JSONObject getFriends(@PathVariable Integer id){
		JSONObject friends = db.findById(id).get().friend;
		System.out.print(friends.toJSONString());
		return friends ;
	}
	
	@GetMapping("/editpass/{id}/{newpass}")
	String changePass(@PathVariable Integer id, @PathVariable String newpass){
		Person user = db.findById(id).get();
		user.setpass(newpass);
		db.save(user);
		return "password has been changed";
	}
	
	Integer findIdbyusername(String username) {
		Integer result = -1;
		List<Person> list = db.findAll();
		Iterator<Person> iterator = list.iterator();
	      while(iterator.hasNext()) {
	    	 Person currentUser = iterator.next();
	    	 String currentUsername = currentUser.username;
	         if (currentUsername.equals(username)) {
	        	 result = currentUser.Id;
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
	
	@RequestMapping("/json.all")
    public @ResponseBody JSONObject jsonAll() {
        JSONObject jsonMain=new JSONObject();                                       
        JSONArray jArray=new JSONArray();                                           
        JSONObject row;
        List<Person> list = db.findAll();
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


