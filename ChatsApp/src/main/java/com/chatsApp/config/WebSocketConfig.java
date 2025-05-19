package com.chatsApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
	
	@Override
	public void registerStompEndpoints (StompEndpointRegistry registry) {
//		registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").addInterceptors(new HttpSessionHandshakeInterceptor()).withSockJS();

	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// TODO Auto-generated method stub
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/group", "/user");
		registry.setUserDestinationPrefix("/user");
	}
}
