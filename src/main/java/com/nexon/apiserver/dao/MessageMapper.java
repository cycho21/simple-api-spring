package com.nexon.apiserver.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MessageMapper {

	@Insert("INSERT INTO messages (senderid, receiverid, chatroomid, messagebody) values(#{senderid}, #{receiverid}, #{chatroomid}, #{messagebody})")
	@Options(useGeneratedKeys = true, keyProperty = "messageid")
	void postMessage(Message message);
	
	@Select("SELECT senderid, receiverid, chatroomid, messagebody FROM messages WHERE messageid = #{messageid}")
	Message checkMessage(@Param("messageid") int messageid);
	
	@Select("SELECT senderid, receiverid, messageid, messagebody FROM messages WHERE chatroomid = #{chatroomid} AND (senderid = #{senderid} OR receiverid = #{receiverid})")
	ArrayList<Message> getMessageByUserId(@Param("chatroomid") int chatroomid, @Param("senderid") int senderid, @Param("receiverid") int receiverid);
	
	@Select("SELECT senderid, receiverid, messageid, messagebody FROM messages WHERE chatroomid = #{chatroomid}")
	ArrayList<Message> getMessages(@Param("chatroomid") int chatroomid);
	
	@Select("SELECT senderid, receiverid, messageid, messagebody FROM messages WHERE chatroomid = #{chatroomid} AND (senderid = #{senderid} OR receiverid = #{receiverid}) AND receiverid NOT LIKE 0") 
	ArrayList<Message> getWhisperByUserId(@Param("chatroomid") int chatroomid, @Param("senderid") int senderid, @Param("receiverid") int receiverid);
	
	@Delete("DELETE FROM messages WHERE chatroomid = #{chatroomid}")
	void deleteMessagesFromDeletedChatroom(@Param("chatroomid") int chatroomid);
}