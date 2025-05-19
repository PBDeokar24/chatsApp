package com.chatsApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatsApp.exception.ChatException;
import com.chatsApp.exception.UserException;
import com.chatsApp.model.Chat;
import com.chatsApp.model.User;
import com.chatsApp.request.SingleChatRequest;
import com.chatsApp.response.ApiResponse;
import com.chatsApp.service.ChatService;
import com.chatsApp.service.GroupChatRequest;
import com.chatsApp.service.UserService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

	@Autowired
	private ChatService chatService;
	@Autowired
	private UserService userService;
	
	public ChatController(ChatService chatService, UserService userService) {
//		super();
		this.chatService = chatService;
		this.userService = userService;
	}
	
	@PostMapping("/single")
	public ResponseEntity<Chat> createChatHandler(@RequestBody SingleChatRequest singleChatRequest, @RequestHeader("Authorization") String jwt) throws UserException{
		
		User reqUser= userService.findUserProfile(jwt);
		
		Chat chat = chatService.createChat(reqUser, singleChatRequest.getUserId());
		
		return new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	@PostMapping("/group")
	public ResponseEntity<Chat> createGroupHandler(@RequestBody GroupChatRequest req, @RequestHeader("Authorization") String jwt) throws UserException{
		
		User reqUser= userService.findUserProfile(jwt);
		
		Chat chat = chatService.createGroup(req, reqUser);
		
		return new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	@GetMapping("/{chatId}")
	public ResponseEntity<Chat> findChatByIdHandler(@PathVariable Integer chatId, @RequestHeader("Authorization") String jwt) throws UserException, ChatException{
		
		Chat chat = chatService.findChatById(chatId);
		
		return new ResponseEntity<Chat>(chat, HttpStatus.OK);
	}
	
	@GetMapping("/user")
	public ResponseEntity<List<Chat>> findAllChatByUserIdHandler(@RequestHeader("Authorization") String jwt) throws UserException{
		
		User reqUser= userService.findUserProfile(jwt);
		
		List<Chat> chats = chatService.findAllChatByUserId(reqUser.getId());
		
		return new ResponseEntity<List<Chat>>(chats, HttpStatus.OK);
	}
	
	@PutMapping("/{chattId}/add/{userId}")
	public ResponseEntity<Chat> addUserToGroupHandler(@PathVariable Integer chatId , @PathVariable Integer userId , @RequestHeader("Authorization") String jwt) throws UserException, ChatException{
		
		User reqUser= userService.findUserProfile(jwt);
		
		Chat chat = chatService.addUserToGroup(userId, chatId, reqUser);
		
		return new ResponseEntity<>(chat, HttpStatus.OK);
	}
	
	@PutMapping("/{chattId}/remove/{userId}")
	public ResponseEntity<Chat> removeUserToGroupHandler(@PathVariable Integer chatId , @PathVariable Integer userId , @RequestHeader("Authorization") String jwt) throws UserException, ChatException{
		
		User reqUser= userService.findUserProfile(jwt);
		
		Chat chat = chatService.removeFromGroup(userId, chatId, reqUser);
		
		return new ResponseEntity<>(chat, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{chattId}")
	public ResponseEntity<ApiResponse> deleteChatHandler(@PathVariable Integer chatId , @RequestHeader("Authorization") String jwt) throws UserException, ChatException{
		
		User reqUser= userService.findUserProfile(jwt);
		
		chatService.deleteChat(chatId, reqUser.getId());
		
		ApiResponse response = new ApiResponse("Chat is deleted Successfully", true);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	
}
