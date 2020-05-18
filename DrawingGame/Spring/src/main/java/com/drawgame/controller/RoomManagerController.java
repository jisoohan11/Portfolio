package com.drawgame.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.websocket.Session;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.drawgame.config.Config;
import com.drawgame.model.Room;
import com.drawgame.model.User;
import com.drawgame.websocket.GameRoomMng;
import com.drawgame.websocket.RoomMng;

@Component
@RestController
public class RoomManagerController {

	RoomMng rm = RoomMng.getInstance();
	GameRoomMng grm = GameRoomMng.getInstance();

	@GetMapping("/roommng/get")
	Map<UUID, Room> getRooms() {
		return rm.getRooms();
	}

	@GetMapping("/roommng/remove/{roomUid}")
	void removeById(@PathVariable UUID roomUid) {
		rm.removeById(roomUid);
	}

	@GetMapping("/roommng/count")
	int getCount() {
		return rm.getCount();
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/roommng/roomuser/{roomUid}")
	JSONObject getRoomUserName(@PathVariable UUID roomUid) {
		JSONObject json = new JSONObject();
		Room room = rm.getRoom(roomUid);
		ArrayList<User> users = room.getUsers();
		ArrayList<String> userNames = new ArrayList<String>();
		for (User user : users) {
			userNames.add(user.getUser());
		}
		json.put("list", userNames);
		return json;
	}

	@GetMapping("/roommng/roomid")
	Map<String, Object> getRoomIdList() {
		Map<String, Object> list = new HashMap<>();
		ArrayList<Map<String, Object>> rooms = new ArrayList<>();
		for (final Map.Entry<UUID, Room> room : rm.getRooms().entrySet()) {
			Map<String, Object> map = new HashMap<>();
			map.put(Config.SESS_ROOM_UID, room.getValue().getId());
			map.put(Config.SESS_ROOM_NAME, room.getValue().getName());
			map.put("roomCount", room.getValue().userCount());
			rooms.add(map);
		}
		list.put("rooms", rooms);
		return list;
	}

	@GetMapping("/roommng/setdrawer/{roomUid}/{userId}")
	String setDrawer(@PathVariable UUID roomUid, @PathVariable String userId) {
		synchronized (RoomMng.class) {
			Room room = rm.getRoom(roomUid);
			for (Session user : room.getUserSessions()) {
				if (user.getUserProperties().get(Config.SESS_USER_ID).toString().equals(userId)) {
					room.setDrawer(user);
					break;
				}
			}
			if (room.getDrawer() != null)
				return room.getDrawer().getUserProperties().get(Config.SESS_USER_ID).toString();

			return "-1";
		}
	}

	@GetMapping("/roommng/runorder/{roomUid}")
	String getDrawer(@PathVariable UUID roomUid) {
		synchronized (RoomMng.class) {
			Room room = new Room();
			System.out.println("test");
//			if (rm.getRoom(roomUid) != null) {
//				room = rm.getRoom(roomUid);
//			} else if (grm.getRoom(roomUid) != null) {
//				room = grm.getRoom(roomUid);
//			} else {
//				System.out.println("failed");
//			}
			room = rm.getRoom(roomUid);
			System.out.println(room);
			System.out.println(room.getUsers());
			System.out.println("test1");
			String id = room.runOrder().getUserProperties().get(Config.SESS_USER_ID).toString();
			System.out.println(id);
			return id;
		}
	}
}
