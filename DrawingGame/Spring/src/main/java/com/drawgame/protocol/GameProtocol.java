package com.drawgame.protocol;

import javax.websocket.Session;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component
public interface GameProtocol {
    public enum TYPE {
        JOIN(0),
        GAMESTART(1),
        CHAT(2),
        DRAW_UPDATE(3),
        SELECT_STATE(4),
        CORANSWER(5),
        BREAK_GAME(6),
        TOOL_BAR(7),
        NEW_WORD(8);

        private int val;
        TYPE(int _val) {
            val = _val;
        }

        public int getVal() {
            return val;
        }
    }

    public enum STATETYPE {
        CLEAR(0);

        private int val;
        STATETYPE(int _val) {
            val = _val;
        }

        public int getVal() {
            return val;
        }
    }

    public void join(Session session) throws Exception;
    public void gameStart(Session session) throws Exception;
    public void chat(Session session, String chat) throws Exception;
    public void drawUpdate(Session session, float x, float y, int init) throws Exception;
    public void selectState(Session session, int state) throws Exception;
    public void correctAnswer(Session session, String correctAnswer) throws Exception;
    public void breakGame(Session session) throws Exception;
    public void broadCast(Session session, JSONObject json) throws Exception;
    public void getNewWord(Session session, String s) throws Exception;
}
