package com.chatsApp.service;

import java.util.List;

import com.chatsApp.exception.UserException;
import com.chatsApp.model.User;
import com.chatsApp.request.UpdateUserRequest;

public interface UserService {

	public User findUserById(Integer id) throws UserException;
	
	public User findUserProfile(String jwt) throws UserException;
	
	public User updateUser(Integer userId, UpdateUserRequest req) throws UserException;
	
	public List<User> searchUser(String query);
	
}
