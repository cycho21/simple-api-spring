package com.nexon.apiserver.dao;

/**
 * Created by Administrator on 2017-02-04.
 */
public class User {
	private int userid;
	private String nickname;

    public User() {
    	this.nickname = null;
    	this.userid = 0;
    }

    public User(String nickname, int userid) {
        this.nickname = nickname;
        this.userid = userid;
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
