package com.nexon.apiserver.handler;

public class SessionStatus {

	private boolean isLoggedIn;
	
	private SessionStatus(StatusBuilder statusBuilder) {
		this.isLoggedIn = statusBuilder.isLoggedIn;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	
	public static class StatusBuilder {
		private boolean isLoggedIn;
		
		public StatusBuilder() {
		}

		public StatusBuilder setLoggedIn(boolean isLoggedIn) {
			this.isLoggedIn = isLoggedIn;
			return this;
		}
		
		public SessionStatus build() {
			return new SessionStatus(this);
		}
	}
	
}
