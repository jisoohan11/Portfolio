package com.drawgame.protocol;

import javax.websocket.Session;

import org.springframework.stereotype.Component;

@Component
public interface LobbyProtocol {
    public enum TYPE {
        ROOM_CREATE(0),
        ROOMINFO_UPDATE(1);

        private int val;
        TYPE(int _val) {
            val = _val;
        }

        public int getVal() {
            return val;
        }
    }

    public void roomCreate(Session session, String name) throws Exception;
    public void roomInfoUpdateResponse(Session session) throws Exception;
}
