package com.drawgame.websocket;

import com.drawgame.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.websocket.Session;

import org.springframework.stereotype.Component;

@Component
public class RoomMng {
	private static RoomMng instance;
	private Map<UUID, Room> mRooms = new HashMap<>();
	private ArrayList<UUID> idList = new ArrayList<UUID>();

	private RoomMng() {
	}

	public static synchronized RoomMng getInstance() {
		if (instance == null) {
			instance = new RoomMng();
		}

		return instance;
	}

	public Map<UUID, Room> getRooms() {
		return mRooms;
	}

	public boolean removeById(UUID roomUid) {
		synchronized (RoomMng.class) {
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
		synchronized (RoomMng.class) {
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
		synchronized (RoomMng.class) {
			if (!mRooms.containsKey(roomUid))
				return null;

			final Room room = mRooms.get(roomUid);
			if (!room.isIn(user))
				return null;

			room.leave(user);
			return room;
		}
	}

	public boolean initReadyCount(UUID roomUid) {
		synchronized (RoomMng.class) {
			if (!mRooms.containsKey(roomUid))
				return false;

			Room room = mRooms.get(roomUid);
			room.initReadyCount();
			return true;
		}
	}

	public Room getRoom(final UUID roomUid) {
		synchronized (RoomMng.class) {
			if (!mRooms.containsKey(roomUid))
				return null;

			return mRooms.get(roomUid);
		}
	}
}
