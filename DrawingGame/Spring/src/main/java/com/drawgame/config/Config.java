package com.drawgame.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@Configuration
public class Config {
	public final static String SESS_USER_ID = "USER_ID";
	public final static String SESS_USER_NAME = "USER_NAME";
	public final static String SESS_ROOM_UID = "USER_ROOM_UID";
	public final static String SESS_ROOM_NAME = "USER_ROOM_NAME";
	public final static int MAX_ROOM_USERS = 4;
	public final static String PROTOCOL_PREFIX = "proto";
	public final static String PROTOCOL_SUC = "suc";
	public final static String SERVER_IP = "10.24.226.190";

	public static byte[] objToByte(Object obj) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
		objStream.writeObject(obj);

		return byteStream.toByteArray();
	}

	public static Object byteToObj(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		ObjectInputStream objStream = new ObjectInputStream(byteStream);

		return objStream.readObject();
	}
}