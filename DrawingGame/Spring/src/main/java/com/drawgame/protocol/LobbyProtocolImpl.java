package com.drawgame.protocol;

import com.drawgame.config.Config;
import com.drawgame.model.Room;
import com.drawgame.websocket.GameRoomMng;
import com.drawgame.websocket.RoomMng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.websocket.Session;

import org.springframework.stereotype.Component;

@Component
public class LobbyProtocolImpl implements LobbyProtocol {
    @Override
    public void roomCreate(Session session, String name) throws Exception {
        UUID roomId = UUID.randomUUID(); // create new room.

        Map<String, Object> map = new HashMap<>();
        map.put(Config.PROTOCOL_PREFIX, TYPE.ROOM_CREATE.getVal());
        map.put(Config.PROTOCOL_SUC, true);
        map.put(Config.SESS_ROOM_NAME, name);
        map.put(Config.SESS_ROOM_UID, roomId);
        session.getBasicRemote().sendText(map.toString());

        System.out.println(roomId + ":: has been created as a room " + name);
    }

    @Override
    public void roomInfoUpdateResponse(Session session) throws Exception {
        ArrayList<Map<String, Object>> rooms = new ArrayList<>();
        for (final Map.Entry<UUID, Room> room : RoomMng.getInstance().getRooms().entrySet()) {
        	if (GameRoomMng.getInstance().getRooms().containsKey(room.getValue().getId())) {
        		continue;
        	}
            Map<String, Object> map = new HashMap<>();
            map.put(Config.SESS_ROOM_UID, room.getValue().getId());
            map.put(Config.SESS_ROOM_NAME, room.getValue().getName());
            map.put("roomCount", room.getValue().userCount());
            rooms.add(map);
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put(Config.PROTOCOL_PREFIX, TYPE.ROOMINFO_UPDATE.getVal());
        map.put(Config.PROTOCOL_SUC, true);
        map.put("rooms", rooms);
        System.out.println(map);
        session.getBasicRemote().sendText(map.toString());
    }
}
