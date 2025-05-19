package com.chatsApp.service;

import java.util.List;

import com.chatsApp.exception.ChatException;
import com.chatsApp.exception.MessageException;
import com.chatsApp.exception.UserException;
import com.chatsApp.model.Message;
import com.chatsApp.model.User;
import com.chatsApp.request.SendMessageRequest;

public interface MessageService {
		
//		public Message sendMessage(SendMessageRequest req) throws UserException, ChatException;
		public Message sendMessage(SendMessageRequest req, User user) throws UserException, ChatException;

		
		public List<Message> getChatsMessages(Integer chatId, User reqUser) throws ChatException, UserException;
	
		public Message findMessageById(Integer messageId) throws MessageException;
		
		public void deleteMessage(Integer messageId, User reqUser) throws MessageException, UserException;

}
