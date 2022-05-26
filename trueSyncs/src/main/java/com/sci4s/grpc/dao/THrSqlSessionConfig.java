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
@MapperScan(value = "com.sci4s.msa.mapper.thr", sqlSessionFactoryRef = "thrSqlSessionFactory")
@EnableTransactionManagement
public class THrSqlSessionConfig {
	
	@Value("${thr.datasource.url}")
	String DB_URL;
	
	@Value("${thr.datasource.username}")
	String DB_USER;
	
	@Value("${thr.datasource.password}")
	String DB_PWD;
	
	@Value("${thr.datasource.driverClassName}")
	String DB_DRIVER;
	
	@Value("${thr.datasource.hikari.minimum-idle}")
	String MINIUM;
	
	@Value("${thr.datasource.maximum-pool-size}")
	String MAXIUM_POOLSIZE;
	
	@Value("${thr.datasource.pool-name}")
	String POOL_NAME;
	
	@Value("${thr.datasource.idle-timeout}")
	String IDLE_TIMEOUT;
	
	@Value("${thr.datasource.max-lifetime}")
	String MAX_LIFETIME;
	
	@Value("${thr.datasource.connection-timeout}")
	String CONNECTION_TIMEOUT;
	
	@Value("${thr.datasource.transaction-isolation}")
	String ISOLATION_LEVEL;
	
	@Value("${thr.datasource.leak-detection-threshold}")
	String LEAK_DETCTION_THRESHOLD;	
	
	@Bean(name = "thrDataSource")
	public DataSource thrDataSource() {
		
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

	@Bean(name = "thrTxManager")
	public PlatformTransactionManager thrTxManager(@Qualifier("thrDataSource") DataSource thrDataSource) {
		return new DataSourceTransactionManager(thrDataSource);
	}

	@Bean(name = "thrSqlSessionFactory")
	public SqlSessionFactory thrSqlSessionFactory(@Qualifier("thrDataSource") DataSource thrDataSource, ApplicationContext applicationContext) throws Exception {

		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(thrDataSource);
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:com/sci4s/msa/mapper/thr/Mapper.xml"));

		return factoryBean.getObject();
	}

	@Bean(name = "thrSqlSessionTemplate", destroyMethod = "clearCache")
	public SqlSessionTemplate thrSqlSessionTemplate(SqlSessionFactory thrSqlSessionFactory) {
		return new SqlSessionTemplate(thrSqlSessionFactory);
	}
}
