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
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.drawgame.config.Config;
import com.drawgame.controller.UserController;
import com.drawgame.protocol.LobbyProtocol;
import com.drawgame.protocol.LobbyProtocolImpl;

@Component
@ServerEndpoint(value = "/lobby/{userID}/{userName}", configurator = CustomConfigurator.class)
public class LobbyWebSocketServer {
	
	List<Session> sessions = new ArrayList<>();
	LobbyProtocol lobbyProtocol = new LobbyProtocolImpl();
	public static Map<Session, String> sessionUsernameMap = new HashMap<>();
	public static Map<String, Session> usernameSessionMap = new HashMap<>();
	
	private final Logger logger = LoggerFactory.getLogger(LobbyWebSocketServer.class);
	
	@OnOpen
	public void onOpen(Session session, @PathParam("userID") String userid, @PathParam("userName") String userName)
			throws IOException {
		logger.info("Entered into Open");
		sessions.add(session);
		Map<String, Object> map = new HashMap<>();
		map.put("proto", "OPEN");
		sessionUsernameMap.put(session, userName);
		usernameSessionMap.put(userName, session);
		session.getUserProperties().put(Config.SESS_USER_ID, userid);
		session.getUserProperties().put(Config.SESS_USER_NAME, userName);
		session.getUserProperties().put(Config.SESS_ROOM_NAME, "Lobby");
		session.getUserProperties().put(Config.SESS_ROOM_UID, LobbyRoomMng.getLobbyID());
		System.out.println(UUID.fromString(session.getUserProperties().get(Config.SESS_ROOM_UID).toString()));
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
	}
	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.info("Entered into Error");
	}
	private boolean handler(final Session session, final JSONObject json) throws Exception {
		if (!json.containsKey(Config.PROTOCOL_PREFIX))
            return false;

        LobbyProtocol.TYPE proto = LobbyProtocol.TYPE.values()[Integer.parseInt(json.get(Config.PROTOCOL_PREFIX).toString())];
        switch (proto) {
            case ROOM_CREATE: {
                if (!json.containsKey("roomName")) {
                    failHandler(session, "roomName");
                    return false;
                }
                
                lobbyProtocol.roomCreate(session, json.get("roomName").toString());
                return true;
            }
            case ROOMINFO_UPDATE: {
                lobbyProtocol.roomInfoUpdateResponse(session);
                return true;
            }
        }

        return false;
	}
	
	private void failHandler(final Session session, final String tag) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put(Config.PROTOCOL_SUC, false);
		map.put("tag", tag);
        session.getBasicRemote().sendText(map.toString());
    }

    public void roomInfoUpdate() throws Exception {
        for (Session session : sessions) {
            lobbyProtocol.roomInfoUpdateResponse(session);
        }
    }
}
