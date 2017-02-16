package com.nexon.apiserver.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ChatroomMapper {
	
	@Insert("INSERT INTO chatroomssnapshot (userid, chatroomid) values(#{userid}, #{chatroomid})")
	void joinChatroom(@Param("userid") int userid, @Param("chatroomid") int chatroomid);
	
	@Insert("INSERT INTO chatrooms (chatroomname, userid) values (#{chatroomname}, #{userid})")
	@Options(useGeneratedKeys = true, keyProperty = "chatroomid")
	void saveChatroom(Chatroom chatroom);
	
	@Select("SELECT * FROM chatrooms WHERE chatroomname = #{chatroomname}")
	Chatroom findChatroomByChatroomname(@Param("chatroomname") String chatroomname);
	
	@Select("SELECT * FROM chatrooms WHERE chatroomid = #{chatroomid}")
	Chatroom findChatroomByChatroomid(@Param("chatroomid") int chatroomid);
	
	@Select("SELECT chatrooms.chatroomid, chatrooms.chatroomname FROM chatrooms INNER JOIN chatroomssnapshot ON chatroomssnapshot.chatroomid = chatrooms.chatroomid WHERE chatroomssnapshot.userid = #{userid}")
	ArrayList<Chatroom> findChatroomsByUserid(@Param("userid") int userid);
	
	@Update("UPDATE chatrooms SET chatroomname = #{chatroomname} WHERE userid = #{userid}")
	void updateChatroom(@Param("chatroomname") String chatroomname, @Param("userid") int userid);
	
	@Update("DELETE FROM chatroomssnapshot WHERE userid = #{userid} AND chatroomid = #{chatroomid}")
	void quitChatroom(@Param("chatroomid") int chatroomid, @Param("userid") int userid);
	
	@Select("SELECT userid FROM chatroomssnapshot WHERE chatroomid = #{chatroomid}")
	ArrayList<User> findUsersFromChatroom(@Param("chatroomid") int chatroomid);
	
	@Delete("DELETE FROM chatrooms WHERE chatroomid = #{chatroomid}")
	void deleteChatroom(@Param("chatroomid") int chatroomid);
	
	@Select("SELECT * FROM chatrooms")
	ArrayList<Chatroom> getChatrooms();
}