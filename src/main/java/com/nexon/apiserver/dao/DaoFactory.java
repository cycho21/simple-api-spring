package com.nexon.apiserver.dao;


import javax.sql.DataSource;

import org.apache.ibatis.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nexon.apiserver.aspect.LoggingAspect;

@Configuration
@EnableAspectJAutoProxy
public class DaoFactory {
	
	@Autowired
	private DataSource dataSource;

	@Bean
	public LoggingAspect aspect() {
		return new LoggingAspect();
	}
	
	@Bean
	public Dao dao() {
		Dao dao = new Dao();
		dao.setJdbcTemplate(jdbcTemplate());
		dao.initialize();
		LogFactory.useSlf4jLogging();
		LogFactory.useLog4JLogging();
		return dao;
	}
	
	public JdbcTemplate jdbcTemplate() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(dataSource);
		return jdbcTemplate;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	

}
