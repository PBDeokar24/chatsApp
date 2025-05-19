package com.chatsApp.service;

import java.util.List;

import com.chatsApp.exception.ChatException;
import com.chatsApp.exception.UserException;
import com.chatsApp.model.Chat;
import com.chatsApp.model.User;

public interface ChatService {
	
	public Chat createChat(User reqUser, Integer userId2) throws UserException;
	
	public Chat findChatById(Integer chatId) throws ChatException;
	
	public List<Chat> findAllChatByUserId (Integer userId) throws UserException;
	
	public Chat createGroup (GroupChatRequest req, User reqUser) throws UserException;
	
	public Chat addUserToGroup (Integer userId, Integer chatId, User reqUser) throws UserException, ChatException;
	
	public Chat renameGroup (Integer chatId, String groupName, User reqUserId) throws ChatException, UserException;
	
	public Chat removeFromGroup (Integer chatId, Integer userId, User reqUser) throws UserException, ChatException;
	
	public void deleteChat (Integer chatId, Integer userId) throws ChatException, UserException;

}
