package com.nexon.apiserver.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by chan8 on 2017-02-08.
 */
@Entity
public class Message {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int messageid = 0;
	@Column
	private int senderid = 0;
	@Column
	private int receiverid = 0;
	@Column
	private String messagebody = null;
	@Column
	private int chatroomid = 0;

	public Message() {
	}

	public int getSenderid() {
		return senderid;
	}

	public void setSenderid(int senderid) {
		this.senderid = senderid;
	}

	public int getReceiverid() {
		return receiverid;
	}

	public void setReceiverid(int receiverid) {
		this.receiverid = receiverid;
	}

	public int getMessageid() {
		return messageid;
	}

	public void setMessageid(int messageid) {
		this.messageid = messageid;
	}

	public String getMessagebody() {
		return messagebody;
	}

	public void setMessagebody(String messagebody) {
		this.messagebody = messagebody;
	}

	public int getChatroomid() {
		return chatroomid;
	}

	public void setChatroomid(int chatroomid) {
		this.chatroomid = chatroomid;
	}
}
