package com.chatsApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chatsApp.exception.UserException;
import com.chatsApp.model.User;
import com.chatsApp.request.UpdateUserRequest;
import com.chatsApp.response.ApiResponse;
import com.chatsApp.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfileHeader(@RequestHeader("Authorization") String token) throws UserException{
		User user = userService.findUserProfile(token);
		
		return new ResponseEntity<User> (user, HttpStatus.ACCEPTED);
	}
	
//	@GetMapping("/{query}")
//	public ResponseEntity<List<User>> searchUserHandler(@PathVariable("query") String q){
//
//		List<User> users = userService.searchUser(q);
//		
//		return new ResponseEntity<List<User>> (users, HttpStatus.OK);
//	}
	
	@GetMapping("/search")
	public ResponseEntity<List<User>> searchUserByName(@RequestParam("name") String name){

		List<User> users = userService.searchUser(name);
		
		return new ResponseEntity<List<User>> (users, HttpStatus.OK);
	}
	
	@PutMapping("/update/{userId}")
	public ResponseEntity<ApiResponse> updateUserHandler(@RequestBody UpdateUserRequest req, @RequestHeader("Authorization") String token) throws UserException{

		User user = userService.findUserProfile(token);
		
		userService.updateUser(user.getId(), req);
		
		ApiResponse response=new ApiResponse("User updated successfully", true);
		
		return new ResponseEntity<ApiResponse> (response, HttpStatus.ACCEPTED);
		
		
		
		
	}
}
