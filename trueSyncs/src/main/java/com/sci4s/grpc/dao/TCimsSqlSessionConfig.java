package com.sci4s.grpc.dao;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(value = "com.sci4s.msa.mapper.tcims", sqlSessionFactoryRef = "tcimsSqlSessionFactory")
@EnableTransactionManagement
public class TCimsSqlSessionConfig {
	
	@Value("${tcims.datasource.url}")
	String DB_URL;
	
	@Value("${tcims.datasource.username}")
	String DB_USER;
	
	@Value("${tcims.datasource.password}")
	String DB_PWD;
	
	@Value("${tcims.datasource.driverClassName}")
	String DB_DRIVER;
	
	@Value("${tcims.datasource.hikari.minimum-idle}")
	String MINIUM;
	
	@Value("${tcims.datasource.maximum-pool-size}")
	String MAXIUM_POOLSIZE;
	
	@Value("${tcims.datasource.pool-name}")
	String POOL_NAME;
	
	@Value("${tcims.datasource.idle-timeout}")
	String IDLE_TIMEOUT;
	
	@Value("${tcims.datasource.max-lifetime}")
	String MAX_LIFETIME;
	
	@Value("${tcims.datasource.connection-timeout}")
	String CONNECTION_TIMEOUT;
	
	@Value("${tcims.datasource.transaction-isolation}")
	String ISOLATION_LEVEL;
	
	@Value("${tcims.datasource.leak-detection-threshold}")
	String LEAK_DETCTION_THRESHOLD;	
	
	@Bean(name = "tcimsDataSource")
	public DataSource tcimsDataSource() {
		
		Properties properties = new Properties();
		properties.put("url", DB_URL);
		properties.put("username", DB_USER);
		properties.put("password", DB_PWD);
		properties.put("driverClassName", DB_DRIVER);		
		properties.put("minimum", MINIUM);
		properties.put("maximumPoolSize", MAXIUM_POOLSIZE);
		properties.put("poolName", POOL_NAME);
		properties.put("idleTimeout", IDLE_TIMEOUT);
		properties.put("maxLifetime", MAX_LIFETIME);
		properties.put("connectionTimeout", CONNECTION_TIMEOUT);
		properties.put("isolationLevel", ISOLATION_LEVEL);
		properties.put("leakDetectionThreshold", LEAK_DETCTION_THRESHOLD);

		return DataSourceCreator.createHikariDataSource(properties);
	}

	@Bean(name = "tcimsTxManager")
	public PlatformTransactionManager tcimsTxManager(@Qualifier("tcimsDataSource") DataSource tcimsDataSource) {
		return new DataSourceTransactionManager(tcimsDataSource);
	}

	@Bean(name = "tcimsSqlSessionFactory")
	public SqlSessionFactory tcimsSqlSessionFactory(@Qualifier("tcimsDataSource") DataSource tcimsDataSource, ApplicationContext applicationContext) throws Exception {

		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(tcimsDataSource);
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:com/sci4s/msa/mapper/tcims/Mapper.xml"));

		return factoryBean.getObject();
	}
	
	@Bean(name = "tcimsSqlSessionTemplate", destroyMethod = "clearCache")
	public SqlSessionTemplate tcimsSqlSessionTemplate(SqlSessionFactory tcimsSqlSessionFactory) {
		return new SqlSessionTemplate(tcimsSqlSessionFactory);
	}
}
