package com.nexon.apiserver.dao;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.nexon.apiserver.aspect.LoggingAdvice;
import com.nexon.apiserver.handler.RandomStringGenerator;
import com.nexon.apiserver.handler.SecurityAlgorithm;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.nexon.apiserver.dao")
public class DaoFactory implements TransactionManagementConfigurer {

	@Autowired
	private DataSource dataSource;

	@Bean
	public LoggingAdvice aspect() {
		return new LoggingAdvice();
	}
	
	@Bean
	public RandomStringGenerator randomStringGenerator() {
		RandomStringGenerator rsg = new RandomStringGenerator();
		rsg.initialize();
		return rsg;
	}
	
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws IOException {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setConfigurationProperties(mybatisProperties());

        return factoryBean;
    }
    
    private Properties mybatisProperties() {
        Properties properties = new Properties();
        properties.put("lazyLoadingEnabled", "true");

        return properties;
    }
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
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
	
    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource);
    }
    
    @Bean
    public SecurityAlgorithm securityAlgorithm() {
    	return new SecurityAlgorithm();
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager();
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
