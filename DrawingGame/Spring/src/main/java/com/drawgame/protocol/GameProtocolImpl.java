package com.drawgame.protocol;

import com.drawgame.config.Config;
import com.drawgame.controller.WordController;
import com.drawgame.model.Room;
import com.drawgame.model.WordTable;
import com.drawgame.websocket.GameRoomMng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.websocket.Session;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class GameProtocolImpl implements GameProtocol {

	@Override
	public void join(Session session) throws Exception {
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));

		if (room == null)
			return;
		
		room.setDrawer(session);
	}

	@Override
	public void gameStart(Session session) throws Exception {
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));

		if (room == null)
			return;

		boolean readyToStart = room.readyToStart();
		if (!readyToStart) {
			System.out.println("has been received gameStart protocol but readyToStart value is not enough.");
			return;
		}

		Map<String, Object> map = new HashMap<>();
		map.put(Config.PROTOCOL_PREFIX, TYPE.GAMESTART.getVal());
		map.put(Config.PROTOCOL_SUC, true);

		Session drawer = room.runOrder();
		map.put("drawerID", drawer.getUserProperties().get(Config.SESS_USER_ID));

		room.setCorAnswer(WordTable.getInstance().randomWord());
		for (final Session user : room.getUserSessions()) {
			if (user == drawer) {
				map.put("word", room.getCorAnswer());
			} else {
				map.put("word", "?");
			}

			user.getBasicRemote().sendText(map.toString());
		}
	}

	@Override
	public void chat(Session session, String chat) throws Exception {
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));

		if (room == null)
			return;

		Map<String, Object> map = new HashMap<>();
		map.put(Config.PROTOCOL_PREFIX, GameProtocol.TYPE.CHAT.getVal());
		map.put(Config.PROTOCOL_SUC, true);
		map.put("userName", session.getUserProperties().get(Config.SESS_USER_NAME));
		map.put("chat", chat);

		for (final Session user : room.getUserSessions()) {
//            if (user.getId().equals(session.getId()))
//                continue;

			user.getBasicRemote().sendText(map.toString());
		}
	}

	@Override
	public void drawUpdate(Session session, float x, float y, int init) throws Exception {
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));

		if (room == null) {
			return;
		}
//        else if (!room.getDrawer().getId().equals(session.getId())) {
//        	System.out.println("no drawer");
//            return;
//        }

		Map<String, Object> map = new HashMap<>();
		map.put(Config.PROTOCOL_PREFIX, TYPE.DRAW_UPDATE.getVal());
		map.put(Config.PROTOCOL_SUC, true);
		map.put("x", x);
		map.put("y", y);
		map.put("step", init);

		for (final Session user : room.getUserSessions()) {
			if (user.getId().equals(session.getId()))
				continue;
//            System.out.println("send text to " + GameWebSocketServer.sessionUsernameMap.get(user));
			user.getBasicRemote().sendText(map.toString());
		}
	}

	@Override
	public void selectState(Session session, int state) throws Exception {
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));

		if (room == null)
			return;
		else if (!room.getDrawer().getId().equals(session.getId()))
			return;

		Map<String, Object> map = new HashMap<>();
		map.put(Config.PROTOCOL_PREFIX, TYPE.SELECT_STATE.getVal());
		map.put(Config.PROTOCOL_SUC, true);
		map.put("state", state);

		for (final Session user : room.getUserSessions()) {
			if (user.getId().equals(session.getId()))
				continue;

			user.getBasicRemote().sendText(map.toString());
		}
	}

	@Override
	public void correctAnswer(Session session, String correctAnswer) throws Exception {
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));

		if (room == null)
			return;

		Map<String, Object> map = new HashMap<>();
		map.put(Config.PROTOCOL_PREFIX, TYPE.CORANSWER.getVal());
		map.put(Config.PROTOCOL_SUC, true);
		map.put("userName", session.getUserProperties().get(Config.SESS_USER_NAME));
		map.put("userId", session.getUserProperties().get(Config.SESS_USER_ID));
		
		boolean correct = room.getCorAnswer().toLowerCase().equals(correctAnswer.toLowerCase());
		map.put("correct", correct ? 1 : 0);
		System.out.println(room.getCorAnswer() + " : " + correctAnswer);
		if (correct) 
			room.incCorrectCount();			

		map.put("gameFinished", room.gameFinished() ? 1 : 0);
        map.put("count", room.getCorrectCount());
		if (correct) {
			for (final Session user : room.getUserSessions()) {
				user.getBasicRemote().sendText(map.toString());
			}
		} else {
			session.getBasicRemote().sendText(map.toString());
		}
	}

	@Override
	public void breakGame(Session session) throws Exception {
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));

		if (room == null)
			return;

		Map<String, Object> map = new HashMap<>();
		map.put(Config.PROTOCOL_PREFIX, TYPE.BREAK_GAME.getVal());
		map.put(Config.PROTOCOL_SUC, true);

		for (final Session user : room.getUserSessions()) {
			if (user.getId().equals(session.getId()))
				continue;

			user.getBasicRemote().sendText(map.toString());
		}

		GameRoomMng.getInstance().removeById(room.getId());
	}

	@Override
	public void broadCast(Session session, JSONObject json) throws Exception {
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));

		if (room == null) {
			return;
		}

		for (final Session user : room.getUserSessions()) {
			if (user.getId().equals(session.getId()))
				continue;

			user.getBasicRemote().sendText(json.toString());
		}
	}

	@Override
	public void getNewWord(Session session, String s) throws Exception {
		System.out.println("getNewWord");
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));
		System.out.println("got room");
		if (room == null) {
			return;
		}
		System.out.println("room not ull");
		WordController wc = new WordController();
		System.out.println("no");
		String newWord = wc.newWord(s);
		room.setCorAnswer(newWord);
		System.out.println(newWord);

		Map<String, Object> map = new HashMap<>();
		map.put(Config.PROTOCOL_PREFIX, TYPE.NEW_WORD.getVal());
		map.put("word", newWord);
		map.put(Config.PROTOCOL_SUC, true);

		session.getBasicRemote().sendText(map.toString());
	}
}
