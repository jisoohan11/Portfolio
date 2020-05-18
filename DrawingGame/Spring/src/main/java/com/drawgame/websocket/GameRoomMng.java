package com.drawgame.websocket;

import com.drawgame.model.Room;
import javax.websocket.Session;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class GameRoomMng {
	private static GameRoomMng instance = null;
	private Map<UUID, Room> mRooms = new HashMap<>();
	private ArrayList<UUID> idList = new ArrayList<UUID>();
	RoomMng rm = RoomMng.getInstance();
	
	private GameRoomMng() {
	}

	public static synchronized GameRoomMng getInstance() {
		if (instance == null) {
			instance = new GameRoomMng();
		}

		return instance;
	}

	public Map<UUID, Room> getRooms() {
		return mRooms;
	}

	public boolean removeById(UUID roomUid) {
		synchronized (GameRoomMng.class) {
			if (!mRooms.containsKey(roomUid))
				return false;

			Room room = mRooms.get(roomUid);
			for (final Session user : room.getUserSessions()) {
				room.leave(user);
			}
			mRooms.remove(roomUid);
			idList.remove(idList.indexOf(roomUid));
			return true;

		}
	}

	public int getCount() {
		return mRooms.size();
	}

	public ArrayList<UUID> getRoomIdList() {
		return idList;
	}

	public Room enter(final UUID roomUid, final Session user, final String roomName) {
		synchronized (GameRoomMng.class) {
			Room room = null;

			if (!mRooms.containsKey(roomUid)) {
				room = new Room(roomName, user, roomUid);
				mRooms.put(roomUid, room);
				idList.add(roomUid);
				return room;
			}
			
			room = mRooms.get(roomUid);
				
			if (room.isIn(user))
				return null;
				
			room.enter(user);
			
			return room;
		}
	}

	public Room leave(final UUID roomUid, final Session user) {
		synchronized (GameRoomMng.class) {
			if (!mRooms.containsKey(roomUid))
				return null;

			final Room room = mRooms.get(roomUid);
			if (!room.isIn(user))
				return null;

			room.leave(user);
			return room;
		}
	}

	public Room getRoom(final UUID roomUid) {
		synchronized (GameRoomMng.class) {
			if (!mRooms.containsKey(roomUid))
				return null;

			return mRooms.get(roomUid);
		}
	}
}
