package com.chatsApp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chatsApp.exception.ChatException;
import com.chatsApp.exception.UserException;
import com.chatsApp.model.Chat;
import com.chatsApp.model.User;
import com.chatsApp.repository.ChatRepository;

@Service
public class ChatServiceImpl implements ChatService{
	
	private ChatRepository chatRepository;
	private UserService userService;
	
	public ChatServiceImpl(ChatRepository chatRepository, UserService userService) {
		this.userService=userService;
		this.chatRepository=chatRepository;
	}

//	@Override
//	public Chat createChat(User reqUser, Integer userId2) throws UserException {
//		
//		User user = userService.findUserById(userId2);
//		
//		Chat isChatExist=chatRepository.findSingleChatByUserIds(user, reqUser);
//		
//		if(isChatExist!= null) {
//			return isChatExist;
//		}
//		
//		Chat chat = new Chat();
//		chat.setCreatedBy(reqUser);
//		chat.getUsers().add(user);
//		chat.getUsers().add(reqUser);
//		chat.setGroup(false);
//		
//		
//		return chat;
//	}
	
	@Override
	public Chat createChat(User reqUser, Integer userId2) throws UserException {
	    User user = userService.findUserById(userId2);
	    
	    Chat isChatExist = chatRepository.findSingleChatByUserIds(user, reqUser);
	    
	    if (isChatExist != null) {
	        return isChatExist;
	    }
	    
	    Chat chat = new Chat();
	    chat.setCreatedBy(reqUser);
	    chat.getUsers().add(user);
	    chat.getUsers().add(reqUser);
	    chat.setGroup(false);

	    Chat savedChat = chatRepository.save(chat); // ✅ fix here
	    return savedChat;
	}


	@Override
	public Chat findChatById(Integer chatId) throws ChatException {
		Optional<Chat> chat=chatRepository.findById(chatId);
		
		if(chat.isPresent()) {
			return chat.get();
		}
		
		throw new ChatException("chat not found with Id : " + chatId);
	}

//	@Override
//	public List<Chat> findAllChatByUserId(Integer userId) throws UserException {
//
//		User user = userService.findUserById(userId);
//		
//		List<Chat> chats = chatRepository.findChatByUserId(user.getId());
//		
//		
//		return chats;
//	}
	
	@Override
	public List<Chat> findAllChatByUserId(Integer userId) throws UserException {
	    User user = userService.findUserById(userId);
	    
	    List<Chat> chats = chatRepository.findChatByUserId(user.getId());

	    // 👇 Add this line to print to console
	    System.out.println("Fetched Chats for userId " + userId + ": " + chats);
	    
	    return chats;
	}


//	@Override
//	public Chat createGroup(GroupChatRequest req, User reqUser) throws UserException {
//		
//		Chat group= new Chat();
//		group.setGroup(true);
//		group.setChat_image(req.getChat_image());
//		group.setChat_name(req.getChat_name());
//		group.setCreatedBy(reqUser);
//		group.getAdmins().add(reqUser);
//
//		for(Integer userId:req.getUserIds()) {
//			User user=userService.findUserById(userId);
//			group.getUsers().add(user);
//		}
//		
//		return group;
//	}
	
	@Override
	public Chat createGroup(GroupChatRequest req, User reqUser) throws UserException {
	    
	    Chat group = new Chat();
	    group.setGroup(true);
	    group.setChat_image(req.getChat_image());
	    group.setChat_name(req.getChat_name());
	    group.setCreatedBy(reqUser);
	    group.getAdmins().add(reqUser);
	    
	 // Add reqUser to users list explicitly
	    group.getUsers().add(reqUser);
	    
	    for (Integer userId : req.getUserIds()) {
	        User user = userService.findUserById(userId);
	        group.getUsers().add(user);
	    }

	    // ✅ Save the group to the database
	    Chat savedGroup = chatRepository.save(group);
	    System.out.println("Group created: " + savedGroup);

	    return savedGroup;
	}


	@Override
	public Chat addUserToGroup(Integer userId, Integer chatId, User reqUser) throws UserException, ChatException {
		
		Optional<Chat> opt=chatRepository.findById(chatId);
		
		User user = userService.findUserById(userId);
		
		if(opt.isPresent()) {
			Chat chat = opt.get();
			if(chat.getAdmins().contains(reqUser)) {
				chat.getUsers().add(user);
				return chatRepository.save(chat);
			}
			else {
				throw new UserException("You are not admin");
			}
		}
		
		throw new ChatException("Chat not found with ID: " + chatId);
	}

	@Override
	public Chat renameGroup(Integer chatId, String groupName, User reqUser) throws ChatException, UserException {
		
		Optional<Chat> opt=chatRepository.findById(chatId);

		if(opt.isPresent()) {
			Chat chat=opt.get();
			if(chat.getUsers().contains(reqUser)) {
				chat.setChat_name(groupName);
				return chatRepository.save(chat);
			}
			
			throw new UserException("You are not member of this group.");
		}
		
		throw new ChatException("Chat not found with ID: " + chatId);
	}

	@Override
	public Chat removeFromGroup(Integer chatId, Integer userId, User reqUser) throws UserException, ChatException {
		Optional<Chat> opt=chatRepository.findById(chatId);
		
		User user = userService.findUserById(userId);
		
		if(opt.isPresent()) {
			Chat chat = opt.get();
			if(chat.getAdmins().contains(reqUser)) {
				chat.getUsers().remove(user);
				return chatRepository.save(chat);
			}
			else if (chat.getUsers().contains(reqUser)) {
				if(user.getId().equals(reqUser.getId())) {
					
				chat.getUsers().remove(user);
				return chatRepository.save(chat);
				}
			}
				throw new UserException("You can't remove other user.");
			
		}
		
		throw new ChatException("Chat not found with ID: " + chatId);
	}

	@Override
	public void deleteChat(Integer chatId, Integer userId) throws ChatException, UserException {
		Optional<Chat> opt=chatRepository.findById(chatId);
		if(opt.isPresent()) {
			Chat chat = opt.get();
			chatRepository.deleteById(chat.getId());
		}
	}

}
