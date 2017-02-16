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
public interface UserMapper {

	@Select("SELECT * FROM users WHERE userid = #{userid}")
	User findByUserid(@Param("userid") int userid);
	
	@Insert("INSERT INTO users (nickname) values (#{nickname})")
	@Options(useGeneratedKeys = true, keyProperty = "userid")
	void saveUser(User user);
	
	@Select("SELECT * FROM users WHERE nickname = #{nickname}")
	User findByNickname(@Param("nickname") String nickname);
	
	@Update("UPDATE users SET nickname = #{nickname} WHERE userid = #{userid}")
	void updateUser(@Param("nickname") String nickname, @Param("userid") int userid);
	
	@Delete("DELETE FROM users WHERE userid = #{userid}")
	void deleteUser(@Param("userid") int userid);

}
