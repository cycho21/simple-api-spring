package com.nexon.apiserver.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Transactional
public interface UserMapper {

	@Select("SELECT * FROM users WHERE userid = #{userid}")
	User findByUserid(@Param("userid") int userid);
	
	@Insert("INSERT INTO users (nickname, password) values (#{nickname}, #{password})")
	@Options(useGeneratedKeys = true, keyProperty = "userid")
	void saveUser(User user);
	
	@Select("SELECT * FROM users WHERE nickname = #{nickname}")
	User findByNickname(@Param("nickname") String nickname);

	@Select("SELECT * FROM users WHERE nickname = #{nickname} AND password = #{password}")
	User findByNicknameAndPassword(@Param("nickname") String nickname, @Param("password") String password);
	
	@Update("UPDATE users SET nickname = #{nickname} WHERE userid = #{userid}")
	void updateUser(@Param("nickname") String nickname, @Param("userid") int userid);
	
	@Delete("DELETE FROM users WHERE userid = #{userid}")
	void deleteUser(@Param("userid") int userid);

	@Select("SELECT * FROM users;")
	ArrayList<User> getAllUsers();
	

}
