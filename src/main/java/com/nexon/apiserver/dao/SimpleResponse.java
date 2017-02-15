package com.nexon.apiserver.dao;

import java.util.ArrayList;

public class SimpleResponse {

	private ArrayList<User> users;
	private ArrayList<Chatroom> chatrooms;
	private ArrayList<Message> messages;
	
	public ArrayList<Chatroom> getChatrooms() {
		return chatrooms;
	}

	public void setChatrooms(ArrayList<Chatroom> chatrooms) {
		this.chatrooms = chatrooms;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	
	
}
