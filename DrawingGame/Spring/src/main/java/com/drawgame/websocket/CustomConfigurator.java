package com.drawgame.websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

@Component
public class CustomConfigurator extends ServerEndpointRegistration.Configurator implements ApplicationContextAware {

	private static volatile BeanFactory context;

	@Override
	public <T> T getEndpointInstance(Class<T> endpoint) throws InstantiationException {
		return context.getBean(endpoint);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		CustomConfigurator.context = applicationContext;
	}
	
	@Override
    public void modifyHandshake(ServerEndpointConfig conf, HandshakeRequest req, HandshakeResponse resp) {
        conf.getUserProperties().put("handshakereq", req);
    }
}