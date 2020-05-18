package drawgame;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.json.simple.JSONObject;

@Entity
class Person implements Serializable {
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	Integer Id;
	
	@Column//(updatable = false, nullable = false)
	String username;
	
	@Column
	String password;
	
	@Column
	JSONObject friend = new JSONObject();
	
	@Column
	String email;
	
	public Integer  getId() { return Id; }
	public String getUser() { return username; }
	public String getpass() { return password; }
	public JSONObject getFriend() {return friend; }
	@SuppressWarnings("unchecked")
	public void addFriend(Integer fid, String fname) {
		friend.put(fid, fname);
	}
	public void deleteFriend(Integer fid) {
		friend.remove(fid);
	}
	public String getEmail() {return email; }
	public void setpass(String password) { this.password = password; }
	
	
}
