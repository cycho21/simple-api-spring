package com.nexon.apiserver.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-02-04.
 */
@Configuration
public class Dao {

	@Autowired
	public UserRepository userRepository;
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

//	private void dropChatTable() {
//		jdbcTemplate.update("DROP TABLE IF EXISTS messages;");
//	}
//
//	private void dropChatroomSnapShotTable() {
//		jdbcTemplate.update("DROP TABLE IF EXISTS chatroomssnapshot;");
//	}
//
//	private void dropChatroomTable() {
//		jdbcTemplate.update("DROP TABLE IF EXISTS chatrooms;");
//	}
//
//	public void dropUsersTable() {
//		jdbcTemplate.update("DROP TABLE IF EXISTS users;");
//	}

	private void createChatTable() {
		String query = new StringBuilder()
				.append("CREATE TABLE IF NOT EXISTS messages (messageid INTEGER PRIMARY KEY, ")
				.append("chatroomid INTEGER,").append("senderid INTEGER,").append("receiverid INTEGER,")
				.append("messagebody VARCHAR(100) not NULL);").toString();
		jdbcTemplate.update(query);
	}

	private void createChatroomSnapShotTable() {
		String query = new StringBuilder().append("CREATE TABLE IF NOT EXISTS chatroomssnapshot ")
				.append("(userid INTEGER,").append("chatroomid INTEGER);").toString();
		jdbcTemplate.update(query);
	}

	public ArrayList<Chatroom> getChatrooms() {
		String query = new StringBuilder()
				.append("SELECT chatroomid, chatroomname ")
				.append("FROM chatrooms ")
				.toString();
		ArrayList<Chatroom> chatroomList = new ArrayList<>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
		
		for (Map<String, Object> row : rows) {
			Chatroom chatroom = new Chatroom();
			chatroom.setChatroomid(Integer.parseInt(row.get("chatroomid").toString()));
			chatroom.setChatroomname(row.get("chatroomname").toString());
			chatroomList.add(chatroom);
		}
		return chatroomList;
	}

	public void createUsersTable() {
		String query = new StringBuilder().append("CREATE TABLE IF NOT EXISTS users ")
				.append("(userid INTEGER PRIMARY KEY,").append("nickname VARACHAR(20) not NULL);").toString();
		jdbcTemplate.update(query);
	}

	public void createChatroomTable() {
		String query = new StringBuilder().append("CREATE TABLE IF NOT EXISTS chatrooms ")
				.append("(chatroomid INTEGER PRIMARY KEY AUTOINCREMENT,").append("userid INTEGER,")
				.append("chatroomname VARCHAR(100) not NULL);").toString();
		jdbcTemplate.update(query);
	}

	public int addUser(String nickname) {
		if (getUser(nickname).getUserid() != 0) {
			System.out.println("Nickname exists!");
			return -1;
		}

//		String query = new StringBuilder().append("INSERT INTO users ").append("(nickname) values (?);").toString();
//		KeyHolder keyHolder = new GeneratedKeyHolder();
//		jdbcTemplate.update(new PreparedStatementCreator() {
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				PreparedStatement preparedStatement = con.prepareStatement(query);
//				preparedStatement.setString(1, nickname);
//				return preparedStatement;
//			}
//		}, keyHolder);
//		int userid = keyHolder.getKey().intValue();
		User user = new User();
		user.setNickname(nickname);
		user = userRepository.save(user);
		int userid = user.getUserid();
		System.out.println(userid + "??????????????????????");
		System.out.println(user.getNickname() + "????????????????????");
		return userid;
	}

	public void joinChatroom(int userid, int chatroomid) {
		String query = new StringBuilder().append("INSERT INTO chatroomssnapshot ")
				.append("(userid, chatroomid) values(?, ?);").toString();
		jdbcTemplate.update(query, new Object[] { userid, chatroomid });
	}

	public User getUser(String nickname) {
		String query = "SELECT userid FROM users WHERE nickname=?;";
		User user = new User();

		try {
			jdbcTemplate.queryForObject(query, new String[] { nickname }, new RowMapper<User>() {
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					user.setUserid(rs.getInt("userid"));
					user.setNickname(nickname);
					return user;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return user;
		}
		return user;
	}

	public int postMessage(int senderid, int receiverid, int chatroomid, String messagebody) {
		String query = new StringBuilder().append("INSERT INTO messages ")
				.append("(senderid, receiverid, chatroomid, messagebody) values(?, ?, ?, ?);").toString();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement preparedStatement = con.prepareStatement(query);
				preparedStatement.setInt(1, senderid);
				preparedStatement.setInt(2, receiverid);
				preparedStatement.setInt(3, chatroomid);
				preparedStatement.setString(4, messagebody);
				return preparedStatement;
			}
		}, keyHolder);
		int messageid = keyHolder.getKey().intValue();
		return messageid;
	}

	public Message checkMessage(int messageid) {
		String query = new StringBuilder().append("SELECT senderid, receiverid, chatroomid, messagebody ")
				.append("FROM messages ").append("WHERE messageid=?;").toString();
		Message message = new Message();
		jdbcTemplate.queryForObject(query, new Object[] { messageid }, new RowMapper<Message>() {
			public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
				message.setChatroomid(rs.getInt("chatroomid"));
				message.setReceiverid(rs.getInt("receiverid"));
				message.setSenderid(rs.getInt("senderid"));
				message.setMessagebody(rs.getString("messagebody"));
				message.setMessageid(messageid);
				return message;
			}
		});
		return message;
	}

	public ArrayList<Message> getMessagesByChatroomId(int chatroomid, int userid) {
		ArrayList<Message> messageList = new ArrayList<>();
		String query = new StringBuilder().append("SELECT senderid, receiverid, messageid, messagebody ")
				.append("FROM messages ").append("WHERE chatroomid=? AND (senderid=? OR receiverid=?);").toString();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, new Object[] { chatroomid, userid, userid });

		for (Map<String, Object> row : rows) {
			Message message = new Message();
			message.setSenderid(Integer.parseInt(row.get("senderid").toString()));
			message.setReceiverid(Integer.parseInt(row.get("receiverid").toString()));
			message.setMessageid(Integer.parseInt(row.get("messageid").toString()));
			message.setMessagebody(row.get("messagebody").toString());
			messageList.add(message);
		}
		return messageList;
	}

	public User getUser(int userid) {
//		String query = "SELECT nickname FROM users WHERE userid=?;";
//
		User user = new User();
//
//		try {
//			jdbcTemplate.queryForObject(query, new Integer[] { userid }, (rs, rowNum) -> {
//				user.setNickname(rs.getString("nickname"));
//				user.setUserid(userid);
//				return user;
//			});
//		} catch (EmptyResultDataAccessException e) {
//			return new User(null, 0);
//		}
//		return user;
		
		for (User tuser : userRepository.findByUserid(userid)) {
			return tuser;
		}
		return user;
	}

	public User updateUser(int userid, String nickname) {
		String query = "UPDATE users SET nickname=? WHERE userid=?;";

		if (getUser(userid).getUserid() != 0) {
			jdbcTemplate.update(query, new Object[] { nickname, userid });
		}

		User user = getUser(nickname);
		return user;
	}

	public void deleteUser(int userid) {
		String query = "DELETE FROM users WHERE userid=?;";

		jdbcTemplate.update(query, new Object[] { userid });
	}

	public int addChatRoom(String chatroomname, int userid) {
		String query = "INSERT INTO chatrooms (chatroomname, userid) values (?, ?);";
		int chatroomid = -1;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement preparedStatement = con.prepareStatement(query);
				preparedStatement.setString(1, chatroomname);
				preparedStatement.setInt(2, userid);
				return preparedStatement;
			}
		}, keyHolder);
		chatroomid = keyHolder.getKey().intValue();
		return chatroomid;
	}

	public Chatroom getChatRoomByNameById(String chatroomname) {
		String query = "SELECT chatroomid, userid FROM chatrooms WHERE chatroomname=?;";
		Chatroom chatroom = new Chatroom();
		try {
			jdbcTemplate.queryForObject(query, new String[] { chatroomname }, new RowMapper<Chatroom>() {
				public Chatroom mapRow(ResultSet rs, int rowNum) throws SQLException {
					chatroom.setChatroomid(rs.getInt("chatroomid"));
					chatroom.setUserid(rs.getInt("userid"));
					chatroom.setChatroomname(chatroomname);
					return chatroom;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return new Chatroom(null, 0, 0);
		}
		return chatroom;
	}

	public Chatroom getChatRoomByNameById(int chatroomid) {
		String query = "SELECT chatroomname, userid FROM chatrooms WHERE chatroomid=?;";
		Chatroom chatroom = new Chatroom();

		jdbcTemplate.queryForObject(query, new Object[] { chatroomid }, new RowMapper<Chatroom>() {
			@Override
			public Chatroom mapRow(ResultSet rs, int rowNum) throws SQLException {
				chatroom.setChatroomname(rs.getString("chatroomname"));
				chatroom.setUserid(rs.getInt("userid"));
				chatroom.setChatroomid(chatroomid);
				return chatroom;
			}
		});
		return chatroom;
	}

	public ArrayList<Chatroom> getChatRoomByUserid(int userid) {
		String query = new StringBuilder().append("SELECT chatrooms.chatroomid, chatrooms.chatroomname ")
				.append("FROM chatrooms INNER JOIN chatroomssnapshot ")
				.append("ON chatroomssnapshot.chatroomid = chatrooms.chatroomid ")
				.append("WHERE chatroomssnapshot.userid=?;").toString();
		
		ArrayList<Chatroom> chatroomList = new ArrayList<>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, new Object[] { userid });
		for (Map<String, Object> row : rows) {
			Chatroom chatroom = new Chatroom();
			chatroom.setChatroomid(Integer.parseInt(row.get("chatroomid").toString()));
			chatroom.setChatroomname(row.get("chatroomname").toString());
			chatroomList.add(chatroom);
		}
		
		return chatroomList;
	}

	public int updateChatroom(String chatroomname, int userid) {
		String query = "UPDATE chatrooms SET chatroomname=? WHERE userid=?;";

		int chatroomid = -1;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement preparedStatement = con.prepareStatement(query);
				preparedStatement.setString(1, chatroomname);
				preparedStatement.setInt(2, userid);
				return preparedStatement;
			}
		}, keyHolder);
		
		chatroomid = keyHolder.getKey().intValue();
		return chatroomid;
	}

	public void quitChatroom(int chatroomid, int userid) {
		String query = "DELETE FROM chatroomssnapshot WHERE userid=? AND chatroomid=?;";
		
		jdbcTemplate.update(query, new Object[] { userid, chatroomid });
	}

	public ArrayList<User> getChatroomJoiner(int chatroomid) {
		String query = "SELECT userid FROM chatroomssnapshot WHERE chatroomid =?;";

		ArrayList<User> userList = new ArrayList<>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, new Object[] { chatroomid });

		for (Map<String, Object> row : rows) {
			User user = new User();
			user.setUserid(Integer.parseInt(row.get("userid").toString()));
			userList.add(user);
		}
		
		for (User user : userList) {
			user.setNickname(getUser(user.getUserid()).getNickname());
		}

		return userList;
	}

	public void deleteChatroom(int chatroomid) {
		String query = "DELETE FROM chatrooms WHERE chatroomid=?;";
		
		jdbcTemplate.update(query, new Object[] { chatroomid });
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
