package com.nexon.apiserver.handler;

public class SessionStatus {

	private boolean isLoggedIn;
	private int userid;
	
	private SessionStatus(StatusBuilder statusBuilder) {
		this.isLoggedIn = statusBuilder.isLoggedIn;
		this.userid = statusBuilder.userid;
	}
	
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	
	public static class StatusBuilder {
		private boolean isLoggedIn;
		private int userid;
		
		public StatusBuilder() {
		}

		public StatusBuilder setLoggedIn(boolean isLoggedIn) {
			this.isLoggedIn = isLoggedIn;
			return this;
		}
		
		public StatusBuilder setUserId(int userid) {
			this.userid = userid;
			return this;
		}
		
		public SessionStatus build() {
			return new SessionStatus(this);
		}
	}
	
}
