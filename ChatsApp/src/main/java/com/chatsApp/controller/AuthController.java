package com.chatsApp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatsApp.config.TokenProvider;
import com.chatsApp.exception.UserException;
import com.chatsApp.model.User;
import com.chatsApp.repository.UserRepository;
import com.chatsApp.request.LoginRequest;
import com.chatsApp.response.AuthResponse;
import com.chatsApp.service.CustomUserService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private TokenProvider tokenProvider;
	private CustomUserService customUserService;
	
	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserService customUserService, TokenProvider tokenProvider) {
		this.userRepository=userRepository;
		this.passwordEncoder=passwordEncoder;
		this.customUserService=customUserService;
		this.tokenProvider=tokenProvider;
	}

//	@PostMapping("/signup")
	@PostMapping(value = "/signup", produces = "application/json")

	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException{
		
		String email =user.getEmail();
		String full_name=user.getFull_name();
		String password=user.getPassword();
		
		User isUser = userRepository.findByEmail(email);
		
		if(isUser!=null) {
			throw new UserException("Email is used with another Account : " + email);
		}
		
		User createUser = new User();
		createUser.setEmail(email);
		createUser.setFull_name(full_name);
		createUser.setPassword(passwordEncoder.encode(password));
		
		userRepository.save(createUser);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt= tokenProvider.generateToken(authentication);
		
		AuthResponse response = new AuthResponse(jwt, true);
		
		
		
		return new ResponseEntity<AuthResponse>(response, HttpStatus.ACCEPTED);
		
	}
	
//	@PostMapping("/signin")
	@PostMapping(value = "/signin", produces = "application/json")

	public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req){
		
		String email =req.getEmail();
		String password=req.getPassword();
		
		Authentication authentication =authenticate(email, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		
		String jwt= tokenProvider.generateToken(authentication);
		AuthResponse response = new AuthResponse(jwt, true);
		
		
		return new ResponseEntity<AuthResponse>(response, HttpStatus.ACCEPTED);
	}
	
	public Authentication authenticate(String userName, String password) {
		UserDetails userDetails = customUserService.loadUserByUsername(userName);
		
		if(userDetails == null) {
			throw new BadCredentialsException("Invalid UserName");
		}
		
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid UserName or Password!");
		}
		
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}
