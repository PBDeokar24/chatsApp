package com.chatsApp.service;

import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes.Name;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.chatsApp.config.TokenProvider;
import com.chatsApp.exception.UserException;
import com.chatsApp.model.User;
import com.chatsApp.repository.UserRepository;
import com.chatsApp.request.UpdateUserRequest;

@Service
public class UserServiceImpl implements UserService{
	
	private UserRepository userRepository;
	private TokenProvider tokenProvider;
	
	public UserServiceImpl(UserRepository userRepository, TokenProvider tokenProvider) {
		this.userRepository=userRepository;
		this.tokenProvider=tokenProvider;
	
	}

	@Override
	public User findUserById(Integer id) throws UserException {
		Optional<User> opt= userRepository.findById(id);
		
		if(opt.isPresent()) {
			return opt.get();
		}
		
		throw new UserException("User Not Found with ID: " + id);
	}

	@Override
	public User findUserProfile(String jwt) throws UserException {
		String email = tokenProvider.getEmailFromToken(jwt);
		
		if(email==null) {
			throw new BadCredentialsException("Received invalid token!");
		}
		User user = userRepository.findByEmail(email);
		
		if(user==null) {
			throw new UserException("User not found with EMAIL: " + email);
		}
		
		return user;
	}

	@Override
	public User updateUser(Integer userId, UpdateUserRequest req) throws UserException {

		User user = findUserById(userId);
		
		if(req.getFull_name()!= null) {
			user.setFull_name(req.getFull_name());
			
		}
		
		if(req.getProfile_picture()!= null) {
			user.setProfile_picture(req.getProfile_picture());
			
		}
		return userRepository.save(user);
	}

	@Override
	public List<User> searchUser(String query) {
		List<User> users = userRepository.searchUser(query);
		
		return users;
	}

}
