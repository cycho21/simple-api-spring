package com.nexon.apiserver.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteDataSource;

@Configuration
public class DaoFactory {
	
	private String testFileName;

	@Bean
	public Dao dao() {
		Dao dao = new Dao();
		dao.setJdbcTemplate(jdbcTemplate());
		dao.initialize();
		return dao;
	}
	
	public ConnectionMaker connectionMaker() {
		 SqliteConnectionMaker connectionMaker = new SqliteConnectionMaker();
		 connectionMaker.setTestFileName(testFileName);
		 return connectionMaker;
	}
	
	public DataSource dataSource() {
		this.testFileName = "test.db";
		SQLiteDataSource dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:./" + testFileName);
		return dataSource;
	}
	
	public JdbcTemplate jdbcTemplate() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(dataSource());
		return jdbcTemplate;
	}

}
