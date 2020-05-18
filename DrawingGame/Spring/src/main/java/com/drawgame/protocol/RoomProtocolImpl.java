package com.drawgame.protocol;

import com.drawgame.config.Config;
import com.drawgame.model.Room;
import com.drawgame.websocket.RoomMng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.websocket.Session;

import org.springframework.stereotype.Component;

@Component
public class RoomProtocolImpl implements RoomProtocol {
    @Override
    public void join(Session session) throws Exception {
        Room room = RoomMng.getInstance().getRoom(UUID.fromString(session.getUserProperties()
                .get(Config.SESS_ROOM_UID).toString()));

        if (room == null)
            return;

        ArrayList<Map<String, Object>> users = new ArrayList<>();
        for (final Session user : room.getUserSessions()) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", user.getUserProperties().get(Config.SESS_USER_ID));
            map.put("userName", user.getUserProperties().get(Config.SESS_USER_NAME));
            users.add(map);
        }

        for (final Session user : room.getUserSessions()) {
            Map<String, Object> map = new HashMap<>();
            map.put(Config.PROTOCOL_PREFIX, TYPE.JOIN.getVal());
            map.put(Config.PROTOCOL_SUC, true);
            map.put("users", users);

            user.getBasicRemote().sendText(map.toString());
        }
    }

    @Override
    public void chat(Session session, String chat) throws Exception {
        Room room = RoomMng.getInstance().getRoom((UUID) session.getUserProperties().get(Config.SESS_ROOM_UID));
        if (room == null)
            return;

        Map<String, Object> map = new HashMap<>();
        map.put(Config.PROTOCOL_PREFIX, TYPE.CHAT.getVal());
        map.put(Config.PROTOCOL_SUC, true);
        map.put("userName", session.getUserProperties().get(Config.SESS_USER_NAME));
        map.put("chat", chat);
        for (final Session user : room.getUserSessions()) {
        	System.out.println("for loop"+user);
//            if (user.getId().equals(session.getId()))
//                continue;
            user.getBasicRemote().sendText(map.toString());
        }
    }

    @Override
    public void ready(Session session, boolean display) throws Exception {
        Room room = RoomMng.getInstance().getRoom((UUID) session.getUserProperties().get(Config.SESS_ROOM_UID));
        
        if (room == null)
            return;

        if (display)
            room.incReadyCount();
        else
            room.decReadyCount();

        Map<String, Object> map = new HashMap<>();
        map.put(Config.PROTOCOL_PREFIX, TYPE.READY.getVal());
        map.put(Config.PROTOCOL_SUC, true);
        map.put("userId", session.getUserProperties().get(Config.SESS_USER_ID));
        map.put("display", display ? 1 : 0);
        map.put("readyToStart", room.readyToStart() ? 1 : 0);
        map.put("count", room.getReadyCount());

        for (final Session user : room.getUserSessions()) {
        	user.getBasicRemote().sendText(map.toString());
        }
    }

    @Override
    public void userExit(Session session) throws Exception {
        Room room = RoomMng.getInstance().getRoom((UUID) session.getUserProperties().get(Config.SESS_ROOM_UID));

        if (room == null)
            return;
        
        Map<String, Object> map = new HashMap<>();
        map.put(Config.PROTOCOL_PREFIX, TYPE.USER_EXIT.getVal());
        map.put(Config.PROTOCOL_SUC, true);
        map.put("userId", session.getUserProperties().get(Config.SESS_USER_ID));
        map.put("userName", session.getUserProperties().get(Config.SESS_USER_NAME));

        for (final Session user : room.getUserSessions()) {
            if (user.getId().equals(session.getId()))
                continue;

            user.getBasicRemote().sendText(map.toString());
        }
        
        room.leave(session);
        if (room.isEmpty()) {
        	RoomMng.getInstance().removeById(room.getId());
        }
    }
}
