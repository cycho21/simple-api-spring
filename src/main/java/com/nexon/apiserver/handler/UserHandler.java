package com.nexon.apiserver.handler;

import com.nexon.apiserver.dao.Chatroom;
import com.nexon.apiserver.dao.Dao;
import com.nexon.apiserver.dao.User;
import com.nexon.apiserver.dao.NicknameValidator;
import com.nexon.apiserver.dao.SimpleResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017-02-04.
 */
@RestController
@RequestMapping(Configuration.BASE_URL + "users")
public class UserHandler {
	
	@Autowired
	private RandomStringGenerator randomStringGenerator;
	
	@Autowired
	private SecurityAlgorithm securityAlgorithm;
	
	private static final String SPECIAL_LETTER = "Nickname must alphanumeric but request nickname contains special letters.";
	private static final String LONGER_THAN_TWENTY = "Nickname must less than 20 characters.";
	private static final String NO_USER = "There is no user that you request.";
	private static final String ALREADY_EXIST = "Request name is already exists.";
	private static final String NOT_FOUND = "User not found...";
	private static final String NOT_LOGGED_IN = "You are not logged in...";
	
	@Autowired
	private Dao dao;
	
	private NicknameValidator nicknameValidator;

	public UserHandler() {
		this.nicknameValidator = new NicknameValidator();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> postUser(@RequestBody User user, HttpServletRequest request) {
		
		if (dao.getUser(user.getNickname()).getUserid() != 0)
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ALREADY_EXIST);
		
		user.setPassword(securityAlgorithm.getSHA256(user.getPassword()));
		int code = nicknameValidator.isValidateName(user.getNickname());
		
		switch (code) {
		
		case NicknameValidator.SPECIAL_LETTER:
			return new ResponseEntity<>(SPECIAL_LETTER, HttpStatus.BAD_REQUEST);
		
		case NicknameValidator.LONGER_THAN_TWENTY:
			return new ResponseEntity<>(LONGER_THAN_TWENTY, HttpStatus.BAD_REQUEST);
		
		case NicknameValidator.ALPHA_NUMERIC:
			User retUser = new User();
			int userid = dao.addUser(user.getNickname(), user.getPassword());
			retUser.setUserid(userid);
			retUser.setNickname(user.getNickname());
			return new ResponseEntity<>(retUser, HttpStatus.OK);
			
		default:
			return null;
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getAllUser(HttpServletRequest request) {
		ArrayList<User> list = null;
		
		list = dao.getAllUser();
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/signout", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> signOut(HttpServletRequest request) {
		String sessionid = request.getHeader("sessionid");
		if (!SimpleSession.getSession().containsKey(sessionid)) {
			return new ResponseEntity<>("You are not logged in", HttpStatus.UNAUTHORIZED);
		} else {
			SimpleSession.getSession().remove(sessionid);
			return new ResponseEntity<>("Sign out succeessful", HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> signIn(@RequestBody User user, HttpServletResponse response, HttpServletRequest request) {
		
		String sessionid = randomStringGenerator.nextRandomString(32).toUpperCase();
		
		user.setPassword(securityAlgorithm.getSHA256(user.getPassword()));
		int userid = dao.signIn(user.getNickname(), user.getPassword()).getUserid();
		if (userid != 0) {
			response.addCookie(new Cookie("sessionid", sessionid));
			user.setUserid(userid);
			
			if (!SimpleSession.getSession().containsKey(sessionid))
				SimpleSession.getSession().put(sessionid, new SessionStatus.StatusBuilder().setLoggedIn(true).setUserId(userid).build());
			
			return new ResponseEntity<>(user, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	public boolean checkSessionid(HttpServletRequest request) {
		String sessionid = request.getHeader("sessionid");
		if (!SimpleSession.getSession().containsKey(sessionid))
			return false;
		else
			return true;
	}

	@RequestMapping(value = "/{userid}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putUser(@PathVariable(value = "userid") int userid, @RequestBody User user, HttpServletRequest request) {
		
		if (!checkSessionid(request))
			return new ResponseEntity<>(NOT_LOGGED_IN, HttpStatus.UNAUTHORIZED);
		
		if (dao.getUser(user.getNickname()).getUserid() != 0)
			return new ResponseEntity<>(ALREADY_EXIST, HttpStatus.CONFLICT);
		
		switch (nicknameValidator.isValidateName(user.getNickname())) {
		
		case NicknameValidator.ALPHA_NUMERIC:
			dao.updateUser(userid, user.getNickname());
			User retUser = dao.getUser(userid);
			return new ResponseEntity<>(retUser, HttpStatus.OK);
			
		case NicknameValidator.SPECIAL_LETTER:
			return new ResponseEntity<>(SPECIAL_LETTER, HttpStatus.BAD_REQUEST);
		
		case NicknameValidator.LONGER_THAN_TWENTY:
			return new ResponseEntity<>(LONGER_THAN_TWENTY, HttpStatus.BAD_REQUEST);
		
		default:
			return null;
		}
	}

	@RequestMapping(value = "/{userid}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteUser(@PathVariable(value = "userid") int userid, HttpServletRequest request) {
		if (dao.getUser(userid).getUserid() != 0) {
			dao.deleteUser(userid);
			return new ResponseEntity<>(null, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(NO_USER, HttpStatus.BAD_REQUEST);
		}
	}
	

	@RequestMapping(value = "/{userid}/chatrooms", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getChatroomsFromSpecificUser(@PathVariable(value = "userid") int userid, HttpServletRequest request) {
		System.out.println(userid);
		ArrayList<Chatroom> chatrooms = dao.getChatRoomByUserid(userid);
		SimpleResponse response = new SimpleResponse();
		response.setChatrooms(chatrooms);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{userid}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getUser(@PathVariable(value = "userid") int userid, HttpServletRequest request) {
        User user = dao.getUser(userid);
        if (user.getNickname() != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
        	return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
        }
	}
	
}