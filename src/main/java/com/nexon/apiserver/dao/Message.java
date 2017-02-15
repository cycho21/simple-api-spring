package com.nexon.apiserver.dao;

/**
 * Created by chan8 on 2017-02-08.
 */
public class Message {
	private int senderid = 0;
	private int receiverid = 0;
	private int messageid = 0;
	private String messagebody = null;
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
