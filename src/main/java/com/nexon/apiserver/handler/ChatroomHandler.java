package com.nexon.apiserver.handler;

import com.nexon.apiserver.dao.Chatroom;
import com.nexon.apiserver.dao.Dao;
import com.nexon.apiserver.dao.Message;
import com.nexon.apiserver.dao.SimpleResponse;
import com.nexon.apiserver.dao.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * Created by chan8 on 2017-02-07.
 */
@RestController
@RequestMapping(Configuration.BASE_URL + "chatrooms")
public class ChatroomHandler {
	private static final String ALREADY_EXIST = "Request name is already exists.";
	private static final String NOT_YOURS = "Request chatroom was not make by you.";

	@Autowired
	private Dao dao;
	
	private Logger logger = Logger.getLogger(ChatroomHandler.class);

	public ChatroomHandler() {
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> postChatrooms(@RequestBody Chatroom chatroom) {
		logger.info(":: Request Method : POST :: URI : /api/v2/chatrooms");
		
		if (chatroom.getChatroomname().length() > 100) {
			chatroom.setChatroomname(chatroom.getChatroomname().substring(0, 100));
		}

		if (dao.getChatRoomByNameById(chatroom.getChatroomname()).getChatroomid() != 0) {
			return new ResponseEntity<>(ALREADY_EXIST, HttpStatus.CONFLICT);
		}

		int chatroomid = dao.addChatRoom(chatroom.getChatroomname(), chatroom.getUserid());
		chatroom.setChatroomid(chatroomid);
		dao.joinChatroom(chatroom.getUserid(), chatroom.getChatroomid());

		return new ResponseEntity<>(chatroom, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getChatroomsList() {
		ArrayList<Chatroom> chatrooms = dao.getChatrooms();
		SimpleResponse response = new SimpleResponse();
		response.setChatrooms(chatrooms);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{chatroomid}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> updateChatroomName(@PathVariable(value = "chatroomid") int chatroomid, @RequestBody Chatroom chatroom) {
		
		if (dao.getChatRoomByNameById(chatroomid).getChatroomname().equals(chatroom.getChatroomname())) {
			return new ResponseEntity<>(ALREADY_EXIST, HttpStatus.CONFLICT);
		}
		
		if (chatroom.getUserid() == dao.getChatRoomByNameById(chatroomid).getUserid()) {
			dao.updateChatroom(chatroom.getChatroomname(), chatroom.getUserid());
			chatroom.setChatroomid(dao.getChatRoomByNameById(chatroom.getChatroomname()).getChatroomid());
		} else {
			return new ResponseEntity<>(NOT_YOURS, HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(chatroom, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{chatroomid}/users", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> joinChatroom(@PathVariable(value = "chatroomid") int chatroomid, @RequestBody User user) {
		int userid = user.getUserid();
		dao.joinChatroom(userid, chatroomid);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{chatroomid}/users/{userid}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> quitChatroom(@PathVariable(value = "chatroomid") int chatroomid, @PathVariable(value = "userid") int userid) {
		dao.quitChatroom(chatroomid, userid);
		if (dao.getChatroomJoiner(chatroomid).size() == 0) {
			dao.deleteChatroom(chatroomid);
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{chatroomid}/users", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getUserFromChatroom(@PathVariable(value = "chatroomid") int chatroomid) {
		ArrayList<User> users = dao.getChatroomJoiner(chatroomid);
		SimpleResponse response = new SimpleResponse();
		response.setUsers(users);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{chatroomid}/messages", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> postMessage(@PathVariable(value = "chatroomid") int chatroomid, @RequestBody Message message) {
		int messageid = dao.postMessage(message.getSenderid(), message.getReceiverid(), chatroomid, message.getMessagebody());
		Message checkMessage = dao.checkMessage(messageid);
		
		if (messageid != 0) {
			checkMessage.setMessageid(messageid);
			return new ResponseEntity<>(checkMessage, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Post Message Error", HttpStatus.BAD_REQUEST);	// TO-DO
		}
	}
	
	@RequestMapping(value = "/{chatroomid}/messages", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getMessage(@PathVariable(value = "chatroomid") int chatroomid, @RequestHeader(value = "userid") int userid) {
		ArrayList<Message> messageArrayList = dao.getMessagesByChatroomId(chatroomid, userid);
		SimpleResponse response = new SimpleResponse();
		response.setMessages(messageArrayList);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
