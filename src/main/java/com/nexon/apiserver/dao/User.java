package com.nexon.apiserver.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017-02-04.
 */
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int userid;
    @Column
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
