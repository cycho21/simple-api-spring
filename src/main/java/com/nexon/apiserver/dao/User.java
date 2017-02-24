package com.nexon.apiserver.dao;

/**
 * Created by Administrator on 2017-02-04.
 */
public class User {
	private int userid;
	private String nickname;
	private String password;
	
    public User() {
    	this.nickname = null;
    	this.userid = 0;
    }
    
    public User(String nickname, int userid) {
    	this.userid = userid;
    	this.nickname = nickname;
    }
    
    public User(int userid, String nickname, String password) {
		super();
		this.userid = userid;
		this.nickname = nickname;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
	public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
