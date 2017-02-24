package com.nexon.apiserver.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-02-04.
 */
@Configuration
@Transactional
@EnableTransactionManagement
public class Dao {
	
	@Autowired
	private ChatroomMapper chatroomMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private MessageMapper messageMapper;
	private Logger logger = Logger.getLogger(Dao.class);
	private JdbcTemplate jdbcTemplate;

	public Dao() {
	}

	public void initialize() {
		// dropUsersTable();
		// dropChatroomTable();
		// dropChatroomSnapShotTable();
		// dropChatTable();
		createUsersTable();
		createChatroomTable();
		createChatroomSnapShotTable();
		createChatTable();
		logger.info("Data Access Object initialized...");
	}

	// private void dropChatTable() {
	// jdbcTemplate.update("DROP TABLE IF EXISTS messages;");
	// }
	//
	// private void dropChatroomSnapShotTable() {
	// jdbcTemplate.update("DROP TABLE IF EXISTS chatroomssnapshot;");
	// }
	//
	// private void dropChatroomTable() {
	// jdbcTemplate.update("DROP TABLE IF EXISTS chatrooms;");
	// }
	//
	// public void dropUsersTable() {
	// jdbcTemplate.update("DROP TABLE IF EXISTS users;");
	// }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createChatTable() {
		String query = new StringBuilder()
				.append("CREATE TABLE IF NOT EXISTS messages (messageid INTEGER PRIMARY KEY AUTO_INCREMENT, ")
				.append("chatroomid INTEGER,").append("senderid INTEGER,").append("receiverid INTEGER,")
				.append("messagebody TEXT);").toString();
		jdbcTemplate.update(query);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createChatroomSnapShotTable() {
		String query = new StringBuilder().append("CREATE TABLE IF NOT EXISTS chatroomssnapshot ")
				.append("(userid INTEGER,").append("chatroomid INTEGER);").toString();
		jdbcTemplate.update(query);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createUsersTable() {
		String query = new StringBuilder().append("CREATE TABLE IF NOT EXISTS users ")
				.append("(userid INTEGER PRIMARY KEY AUTO_INCREMENT,")
				.append("nickname VARCHAR(20) not NULL, ")
				.append("password VARCHAR(50) not null);")
				.toString();
		jdbcTemplate.update(query);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createChatroomTable() {
		String query = new StringBuilder().append("CREATE TABLE IF NOT EXISTS chatrooms ")
				.append("(chatroomid INTEGER PRIMARY KEY AUTO_INCREMENT,").append("userid INTEGER,")
				.append("chatroomname VARCHAR(100) not NULL);").toString();
		jdbcTemplate.update(query);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int addUser(String nickname, String password) {
		if (getUser(nickname).getUserid() != 0) {
			logger.error(":: Add user :: Requested name is exist ::");
			return -1;
		}
		
		User user = new User();
		user.setNickname(nickname);
		user.setPassword(password);
		userMapper.saveUser(user);
		return user.getUserid();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ArrayList<Chatroom> getChatrooms() {
		ArrayList<Chatroom> chatroomList = chatroomMapper.getChatrooms();
		return chatroomList;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void joinChatroom(int userid, int chatroomid) {
		chatroomMapper.joinChatroom(userid, chatroomid);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public User getUser(String nickname) {
		User user = userMapper.findByNickname(nickname);
		if (user == null) {
			return new User();
		} else {
			return user;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ArrayList<User> getAllUser() {
		ArrayList<User> users = userMapper.getAllUsers();
		return users;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public User signIn(String nickname, String password) {
		User user = userMapper.findByNicknameAndPassword(nickname, password);
		if (user == null) {
			return new User();
		} else {
			return user;
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int postMessage(int senderid, int receiverid, int chatroomid, String messagebody) {
		Message message = new Message();
		message.setSenderid(senderid);
		message.setReceiverid(receiverid);
		message.setChatroomid(chatroomid);
		message.setMessagebody(messagebody);
		messageMapper.postMessage(message);
		return message.getMessageid();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Message checkMessage(int messageid) {
		return messageMapper.checkMessage(messageid);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ArrayList<Message> getMessagesByChatroomId(int chatroomid, int userid) {
		return messageMapper.getMessageByUserId(chatroomid, userid, userid);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public User getUser(int userid) {
		User user = userMapper.findByUserid(userid);
		if (user == null) {
			return new User(null, 0);
		} else {
			return user;
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public User updateUser(int userid, String nickname) {
		userMapper.updateUser(nickname, userid);
		return userMapper.findByNickname(nickname);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteUser(int userid) {
		userMapper.deleteUser(userid);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int addChatRoom(String chatroomname, int userid) {
		Chatroom chatroom = new Chatroom();
		chatroom.setChatroomname(chatroomname);
		chatroom.setUserid(userid);
		chatroomMapper.saveChatroom(chatroom);
		return chatroom.getChatroomid();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Chatroom getChatRoomByNameById(String chatroomname) {
		Chatroom chatroom = chatroomMapper.findChatroomByChatroomname(chatroomname);
		if (chatroom == null) {
			return new Chatroom(null, 0, 0);
		} else {
			return chatroom;
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Chatroom getChatRoomByNameById(int chatroomid) {
		return chatroomMapper.findChatroomByChatroomid(chatroomid);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ArrayList<Chatroom> getChatRoomByUserid(int userid) {
		return chatroomMapper.findChatroomsByUserid(userid);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int updateChatroom(String chatroomname, int userid) {
		int chatroomid = -1;
		chatroomMapper.updateChatroom(chatroomname, userid);
		chatroomid = getChatRoomByNameById(chatroomname).getChatroomid();
		return chatroomid;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void quitChatroom(int chatroomid, int userid) {
		chatroomMapper.quitChatroom(chatroomid, userid);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ArrayList<User> getChatroomJoiner(int chatroomid) {
		ArrayList<User> userList = chatroomMapper.findUsersFromChatroom(chatroomid);
		for (User user : userList)
			user.setNickname(getUser(user.getUserid()).getNickname());
		return userList;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteChatroom(int chatroomid) {
		chatroomMapper.deleteChatroom(chatroomid);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}



}
