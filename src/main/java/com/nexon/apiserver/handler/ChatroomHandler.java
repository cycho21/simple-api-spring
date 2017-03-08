package com.nexon.apiserver.handler;

import com.nexon.apiserver.dao.Chatroom;
import com.nexon.apiserver.dao.Dao;
import com.nexon.apiserver.dao.Message;
import com.nexon.apiserver.dao.SimpleResponse;
import com.nexon.apiserver.dao.User;
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
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	public ChatroomHandler() {
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> postChatrooms(@RequestBody Chatroom chatroom, HttpServletRequest request) {
		String sessionid = null;
		if (request.getHeader("sessionid") == null) {
			return new ResponseEntity<>("Login needed", HttpStatus.UNAUTHORIZED);
		} else {
			sessionid = request.getHeader("sessionid");
		}

		if (chatroom.getChatroomname().length() > 100) {
			chatroom.setChatroomname(chatroom.getChatroomname().substring(0, 100));
		}

		if (dao.getChatRoomByNameById(chatroom.getChatroomname()).getChatroomid() != 0) {
			return new ResponseEntity<>(ALREADY_EXIST, HttpStatus.CONFLICT);
		}

		int userid = SimpleSession.getSession().get(sessionid).getUserid();

		int chatroomid = dao.addChatRoom(chatroom.getChatroomname(), userid);
		chatroom.setChatroomid(chatroomid);

		dao.joinChatroom(userid, chatroom.getChatroomid());
		return new ResponseEntity<>(chatroom, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getChatroomsList(HttpServletRequest request) {
		ArrayList<Chatroom> chatrooms = dao.getChatrooms();
		SimpleResponse response = new SimpleResponse();
		response.setChatrooms(chatrooms);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{chatroomid}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> updateChatroomName(@PathVariable(value = "chatroomid") int chatroomid,
			@RequestBody Chatroom chatroom, HttpServletRequest request) {
		String sessionid = null;
		if (request.getHeader("sessionid") == null) {
			return new ResponseEntity<>("Login needed", HttpStatus.UNAUTHORIZED);
		} else {
			sessionid = request.getHeader("sessionid");
		}

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
	public ResponseEntity<?> joinChatroom(@PathVariable(value = "chatroomid") int chatroomid, @RequestBody User user,
			HttpServletRequest request) {
		String sessionid = null;
		if (request.getHeader("sessionid") == null) {
			return new ResponseEntity<>("Login needed", HttpStatus.UNAUTHORIZED);
		} else {
			sessionid = request.getHeader("sessionid");
		}

		int userid = user.getUserid();

		ArrayList<Chatroom> temp = dao.getChatRoomByUserid(userid);
		for (Chatroom tChat : temp) {
			if (tChat.getChatroomid() == chatroomid)
				return new ResponseEntity<>("You are already joind", HttpStatus.BAD_REQUEST);
		}

		dao.joinChatroom(userid, chatroomid);
		Chatroom chatroom = dao.getChatRoomByNameById(chatroomid);
		return new ResponseEntity<>(chatroom, HttpStatus.OK);
	}

	@RequestMapping(value = "/{chatroomid}/users/{userid}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> quitChatroom(@PathVariable(value = "chatroomid") int chatroomid,
			@PathVariable(value = "userid") int userid, HttpServletResponse response, HttpServletRequest request) {
		String sessionid = null;

		if (request.getHeader("sessionid") == null)
			return new ResponseEntity<>("Login needed", HttpStatus.UNAUTHORIZED);
		else
			sessionid = request.getHeader("sessionid");
		
		dao.quitChatroom(chatroomid, userid);

		if (dao.getChatroomJoiner(chatroomid).size() == 0) {
			dao.deleteChatroom(chatroomid);
			dao.deleteMessages(chatroomid);
			response.setHeader("deleted", "true");
			return new ResponseEntity<>("deleted", HttpStatus.OK);
		}
		
		response.setHeader("deleted", "false");
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/{chatroomid}/users", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getUserFromChatroom(@PathVariable(value = "chatroomid") int chatroomid,
			HttpServletRequest request) {
		ArrayList<User> users = dao.getChatroomJoiner(chatroomid);
		SimpleResponse response = new SimpleResponse();
		response.setUsers(users);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{chatroomid}/messages", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> postMessage(@PathVariable(value = "chatroomid") int chatroomid,
			@RequestBody Message message, HttpServletRequest request) {
		String sessionid = null;
		if (request.getHeader("sessionid") == null) {
			return new ResponseEntity<>("Login needed", HttpStatus.UNAUTHORIZED);
		} else {
			sessionid = request.getHeader("sessionid");
		}

		int messageid = dao.postMessage(message.getSenderid(), message.getReceiverid(), chatroomid,
				message.getMessagebody());
		Message checkMessage = dao.checkMessage(messageid);
		checkMessage.setSendernickname(dao.getUser(checkMessage.getSenderid()).getNickname());
		if (messageid != 0) {
			checkMessage.setMessageid(messageid);
			return new ResponseEntity<>(checkMessage, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Post Message Error", HttpStatus.BAD_REQUEST); // TO-DO
		}
	}

	@RequestMapping(value = "/{chatroomid}/messages", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getMessage(@PathVariable(value = "chatroomid") int chatroomid,
			@RequestHeader(value = "userid") int userid, HttpServletRequest request) {
		String sessionid = null;
		if (request.getHeader("sessionid") == null) {
			return new ResponseEntity<>("Login needed", HttpStatus.UNAUTHORIZED);
		} else {
			sessionid = request.getHeader("sessionid");
		}

		ArrayList<Message> messageArrayList = dao.getMessages(chatroomid);

		for (Message message : messageArrayList) {
			message.setSendernickname(dao.getUser(message.getSenderid()).getNickname());
		}

		SimpleResponse response = new SimpleResponse();
		response.setMessages(messageArrayList);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{chatroomid}/messages/whisper", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getWhisper(@PathVariable(value = "chatroomid") int chatroomid,
			@RequestHeader(value = "userid") int userid, HttpServletRequest request) {
		String sessionid = null;
		if (request.getHeader("sessionid") == null) {
			return new ResponseEntity<>("Login needed", HttpStatus.UNAUTHORIZED);
		} else {
			sessionid = request.getHeader("sessionid");
		}

		ArrayList<Message> messageArrayList = dao.getWhispersByUserId(chatroomid, userid);

		for (Message message : messageArrayList) {
			message.setSendernickname(dao.getUser(message.getSenderid()).getNickname());
		}

		SimpleResponse response = new SimpleResponse();
		response.setMessages(messageArrayList);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
