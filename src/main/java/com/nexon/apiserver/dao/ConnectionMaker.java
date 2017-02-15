package com.nexon.apiserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface ConnectionMaker {
	public Connection makeConnection();
	public PreparedStatement preparedStatement(String sql);
}
