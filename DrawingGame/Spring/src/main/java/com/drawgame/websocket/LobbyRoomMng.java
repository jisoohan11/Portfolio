package com.drawgame.websocket;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class LobbyRoomMng {
	
	public static UUID lobbyID;
    private static LobbyRoomMng instance;
	
	private LobbyRoomMng() {
	}
    
    public static synchronized LobbyRoomMng getInstance() {
        if (instance == null) {
            instance = new LobbyRoomMng();
        }

        return instance;
    }
    
    public static synchronized UUID getLobbyID() {
    	if (lobbyID == null) {
    		lobbyID = UUID.randomUUID();
        }
    	
    	return lobbyID;
    }
}
