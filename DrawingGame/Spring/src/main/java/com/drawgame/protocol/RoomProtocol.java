package com.drawgame.protocol;

import javax.websocket.Session;

import org.springframework.stereotype.Component;

@Component
public interface RoomProtocol {
    public enum TYPE {
        JOIN(0),
        CHAT(1),
        READY(2),
        USER_EXIT(3);

        private int val;
        TYPE(int _val) {
            val = _val;
        }

        public int getVal() {
            return val;
        }
    }

    public void join(Session session) throws Exception;
    public void chat(Session session, String chat) throws Exception;
    public void ready(Session session, boolean display) throws Exception;
    public void userExit(Session session) throws Exception;
}
