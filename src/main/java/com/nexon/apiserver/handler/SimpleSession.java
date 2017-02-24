package com.nexon.apiserver.handler;

import java.util.HashMap;

public class SimpleSession {
	private static HashMap<String, SessionStatus> simpleSet;
	
	public static HashMap<String, SessionStatus> getSession() {
		if (simpleSet == null) {
			simpleSet = new HashMap<String, SessionStatus>();
			return simpleSet;
		} else {
			return simpleSet;
		}
	}
}