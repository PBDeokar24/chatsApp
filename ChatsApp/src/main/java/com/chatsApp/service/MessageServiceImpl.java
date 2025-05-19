package com.chatsApp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chatsApp.exception.ChatException;
import com.chatsApp.exception.MessageException;
import com.chatsApp.exception.UserException;
import com.chatsApp.model.Chat;
import com.chatsApp.model.Message;
import com.chatsApp.model.User;
import com.chatsApp.repository.MessageRepository;
import com.chatsApp.request.SendMessageRequest;

@Service
public class MessageServiceImpl implements MessageService {
	
	public MessageRepository messageRepository;
	public UserService userService;
	public ChatService chatService;
	
	

	public MessageServiceImpl(MessageRepository messageRepository, UserService userService, ChatService chatService) {
//		super();
		this.messageRepository = messageRepository;
		this.userService = userService;
		this.chatService = chatService;
	}

//	@Override
//	public Message sendMessage(SendMessageRequest req) throws UserException, ChatException {
//		
//		User user = userService.findUserById(req.getChatId());
//		Chat chat= chatService.findChatById(req.getChatId());
//		
//		Message message = new Message();
//		message.setChat(chat);
//		message.setUser(user);
//		message.setContent(req.getContent());
//		message.setTimeStamp(LocalDateTime.now());
//		
//		
//		return message;
//	}
	
	@Override
	public Message sendMessage(SendMessageRequest req, User user) throws UserException, ChatException {
	    // Here, the user is already passed from the controller
	    Chat chat = chatService.findChatById(req.getChatId());

	    // Create and set the message properties
	    Message message = new Message();
	    message.setChat(chat);
	    message.setUser(user);  // Using the passed 'user' object
	    message.setContent(req.getContent());
	    message.setTimeStamp(LocalDateTime.now());

	    // Save the message (if you want to persist it in DB)
	    messageRepository.save(message);

	    return message;
	}


	@Override
	public List<Message> getChatsMessages(Integer chatId, User reqUser) throws ChatException, UserException {
		Chat chat= chatService.findChatById(chatId);
		
		if(!chat.getUsers().contains(reqUser)) {
			throw new UserException("You are not releted to this Chat : " + chat.getId());
		}
		
		List<Message> messages = messageRepository.findByChatId(chat.getId());
		
		
		return messages;
	}

	@Override
	public Message findMessageById(Integer messageId) throws MessageException {
		Optional<Message> opt = messageRepository.findById(messageId);
		
		if(opt.isPresent()) {
			return opt.get();
		}
		
		
		throw new MessageException("Message not found with ID : " + messageId);
	}

	@Override
	public void deleteMessage(Integer messageId, User reqUser) throws MessageException, UserException{
		Message message = findMessageById(messageId);
		
		if(message.getUser().getId().equals(reqUser.getId())) {
			messageRepository.deleteById(messageId);
		}
		
		throw new UserException("You can't delete another user's message " + reqUser.getFull_name());
	}

}
