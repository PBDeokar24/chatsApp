package com.chatsApp.controller;

import java.net.http.HttpRequest;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatsApp.exception.ChatException;
import com.chatsApp.exception.MessageException;
import com.chatsApp.exception.UserException;
import com.chatsApp.model.Message;
import com.chatsApp.model.User;
import com.chatsApp.request.SendMessageRequest;
import com.chatsApp.response.ApiResponse;
import com.chatsApp.service.MessageService;
import com.chatsApp.service.UserService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
	
	private MessageService messageService;
	private UserService userService;
	
	public MessageController(MessageService messageService, UserService userService) {
//		super();
		this.messageService = messageService;
		this.userService = userService;
	}
	
//	@PostMapping("/create")
//	public ResponseEntity<Message> sendMessageHandler(@RequestBody SendMessageRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ChatException{
//		
//		User user = userService.findUserProfile(jwt);
//		
//		req.setUserId(user.getId());
//		Message message= messageService.sendMessage(req);
//		
//		return new ResponseEntity<Message>(message, HttpStatus.OK);
//	}
	
	@PostMapping("/create")
	public ResponseEntity<Message> sendMessageHandler(@RequestBody SendMessageRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ChatException {

	    User user = userService.findUserProfile(jwt);

	    // do not set req.setUserId(user.getId());
	    // directly call messageService.sendMessage(req, user);

	    Message message = messageService.sendMessage(req, user);

	    return new ResponseEntity<>(message, HttpStatus.OK);
	}

	
	@GetMapping("/chat/{chatId}")
	public ResponseEntity<List<Message>> getChatsMessagesHandler(@PathVariable Integer chatId ,@RequestHeader("Authorization") String jwt) throws UserException, ChatException{
		
		User user = userService.findUserProfile(jwt);
		
		List<Message> messages= messageService.getChatsMessages(chatId, user);
		
		return new ResponseEntity<>(messages, HttpStatus.OK);
	}
	
	@DeleteMapping("/{messageId}")
	public ResponseEntity<ApiResponse> deleteMessagesHandler(@PathVariable Integer messageId ,@RequestHeader("Authorization") String jwt) throws UserException, MessageException{
		
		User user = userService.findUserProfile(jwt);
		
		messageService.deleteMessage(messageId, user);
		
		ApiResponse response = new ApiResponse("Message Deleted Successfully", false);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
