package com.chatsApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chatsApp.model.Message;

@Controller
public class RealTimeChat {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/message")
    @SendTo("/group/public") // Optional — just for testing
    public Message receiveMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSend("/group/" + message.getChat().getId(), message);
        return message;
    }
}
