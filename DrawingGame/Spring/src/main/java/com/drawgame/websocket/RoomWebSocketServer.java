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
import com.drawgame.protocol.RoomProtocol;
import com.drawgame.protocol.RoomProtocolImpl;

@Component
@ServerEndpoint(value = "/room/{userID}/{userName}/{roomName}/{roomID}", configurator = CustomConfigurator.class)
public class RoomWebSocketServer {
	List<Session> sessions = new ArrayList<>();
	RoomProtocol roomProtocol = new RoomProtocolImpl();
	public static Map<Session, String> sessionUsernameMap = new HashMap<>();
	public static Map<String, Session> usernameSessionMap = new HashMap<>();
	
	private final Logger logger = LoggerFactory.getLogger(RoomWebSocketServer.class);
	
	@OnOpen
	public void onOpen(Session session, @PathParam("userID") String userid, @PathParam("userName") String userName, @PathParam("roomName") String roomName, @PathParam("roomID") String roomID)
			throws IOException {
		logger.info("Entered into Open");
		sessions.add(session);
		Map<String, Object> map = new HashMap<>();
		map.put("proto", "OPEN");
		sessionUsernameMap.put(session, userName);
		usernameSessionMap.put(userName, session);
		UUID roomUid = UUID.fromString(roomID);
		session.getUserProperties().put(Config.SESS_USER_ID, userid);
		session.getUserProperties().put(Config.SESS_USER_NAME, userName);
		session.getUserProperties().put(Config.SESS_ROOM_NAME,roomName);
		session.getUserProperties().put(Config.SESS_ROOM_UID, roomUid);
		String room = session.getUserProperties().get(Config.SESS_ROOM_NAME).toString();
		RoomMng.getInstance().enter(roomUid, session, room);
//		System.out.println(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));
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
        handler(session, json);
	}
	
	@OnClose
	public void onClose(Session session) throws Exception {
		logger.info("Entered into Close");

		String userName = sessionUsernameMap.get(session);
		sessions.remove(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(userName);
		
		roomProtocol.userExit(session);
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) throws Exception {
		logger.info("Entered into Error");
		roomProtocol.userExit(session);
	}
	private boolean handler(final Session session, final JSONObject json) throws Exception {
		if (!json.containsKey(Config.PROTOCOL_PREFIX))
			return false;
		
		RoomProtocol.TYPE proto = RoomProtocol.TYPE.values()[Integer.parseInt(json.get(Config.PROTOCOL_PREFIX).toString())];
        switch (proto) {
            case JOIN: {
                roomProtocol.join(session);
                break;
            }
            case CHAT: {
                roomProtocol.chat(session, json.get("chat").toString());
                break;
            }
            case READY: {
                boolean display = Integer.parseInt(json.get("display").toString()) == 1;
                roomProtocol.ready(session, display);
                break;
            }
            case USER_EXIT: {
            	roomProtocol.userExit(session);
            	break;
            }
		default:
			break;
        }

        return false;
	}
}
