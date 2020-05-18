package com.drawgame.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.drawgame.config.Config;
import com.drawgame.controller.UserController;
import com.drawgame.model.Room;
import com.drawgame.protocol.GameProtocol;
import com.drawgame.protocol.GameProtocolImpl;

@Component
@ServerEndpoint(value = "/game/{userID}/{userName}/{roomName}/{gameID}", configurator = CustomConfigurator.class)
public class GameWebSocketServer {

	List<Session> sessions = new ArrayList<>();
	GameProtocol gameProtocol = new GameProtocolImpl();
	public static Map<Session, String> sessionUsernameMap = new HashMap<>();
	public static Map<String, Session> usernameSessionMap = new HashMap<>();

	private final Logger logger = LoggerFactory.getLogger(GameWebSocketServer.class);

	@OnOpen
	public void onOpen(Session session, @PathParam("userID") String userid, @PathParam("userName") String userName,@PathParam("roomName") String roomName, @PathParam("gameID") String roomId)
			throws IOException {
		logger.info("Entered into Open");
		sessions.add(session);
		Map<String, Object> map = new HashMap<>();
		map.put("proto", "OPEN");
		UUID roomUid = UUID.fromString(roomId);
		sessionUsernameMap.put(session, userName);
		usernameSessionMap.put(userName, session);
		session.getUserProperties().put(Config.SESS_USER_ID, userid);
		session.getUserProperties().put(Config.SESS_USER_NAME, userName);
		session.getUserProperties().put(Config.SESS_ROOM_NAME, roomName);
		session.getUserProperties().put(Config.SESS_ROOM_UID, roomUid);
		System.out.println(roomUid);
		GameRoomMng.getInstance().enter(roomUid, session, roomName);
		try {
			session.getBasicRemote().sendText(map.toString());
		} catch (IOException e) {
			logger.info("Exception: " + e.getMessage().toString());
			e.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(Session session, String message) throws Exception {
		logger.info("Entered into Message: Got Message:" + message);
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(message);
		
//    	System.out.println("got Message: "+message);
//	    broadcast(message);
		handler(session, json);
	}

	@OnClose
	public void onClose(Session session) throws Exception {
		logger.info("Entered into Close");
		Room room = GameRoomMng.getInstance()
				.getRoom(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));
		
		String userName = sessionUsernameMap.get(session);
		sessions.remove(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(userName);
		
		room.leave(session);
		
		if (room.getDrawer().equals(session))
			gameProtocol.breakGame(session);

//    	String message= username + " disconnected";
//      broadcast(message);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.info("Entered into Error");
	}

//	private static void broadcast(String message) throws IOException {
//		sessionUsernameMap.forEach((session, username) -> {
//			synchronized (session) {
//				try {
//					session.getBasicRemote().sendText(message);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	private boolean handler(final Session session, final JSONObject json) throws Exception {
		if (!json.containsKey(Config.PROTOCOL_PREFIX))
			return false;
		System.out.println("handler enter");
		GameProtocol.TYPE proto = GameProtocol.TYPE.values()[Integer
				.parseInt(json.get(Config.PROTOCOL_PREFIX).toString())];
		switch (proto) {
		case JOIN: {
			gameProtocol.join(session);
			break;
		}
		case GAMESTART: {
			gameProtocol.gameStart(session);
			break;
		}
		case CHAT: {
			gameProtocol.chat(session, json.get("chat").toString());
			break;
		}
		case DRAW_UPDATE: {
			gameProtocol.drawUpdate(session, Float.parseFloat(json.get("x").toString()),
					Float.parseFloat(json.get("y").toString()), Integer.parseInt(json.get("step").toString()));
			break;
		}
		case SELECT_STATE: {
			gameProtocol.selectState(session, Integer.parseInt(json.get("state").toString()));
			break;
		}
		case CORANSWER: {
			gameProtocol.correctAnswer(session, json.get("word").toString());
			break;
		}
		case TOOL_BAR: {
			gameProtocol.broadCast(session, json);
			break;
		}
		case NEW_WORD: {
			System.out.println("new word case");
			gameProtocol.getNewWord(session, json.get("word").toString());
			break;
		}
		default:
			break;
		}

		return false;
	}
}
