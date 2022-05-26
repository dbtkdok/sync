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
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(value = "com.sci4s.msa.mapper.tsys", sqlSessionFactoryRef = "tsysSqlSessionFactory")
@EnableTransactionManagement
public class TSysSqlSessionConfig {
	
	@Value("${tsys.datasource.url}")
	String DB_URL;
	
	@Value("${tsys.datasource.username}")
	String DB_USER;
	
	@Value("${tsys.datasource.password}")
	String DB_PWD;
	
	@Value("${tsys.datasource.driverClassName}")
	String DB_DRIVER;
	
	@Value("${tsys.datasource.hikari.minimum-idle}")
	String MINIUM;
	
	@Value("${tsys.datasource.maximum-pool-size}")
	String MAXIUM_POOLSIZE;
	
	@Value("${tsys.datasource.pool-name}")
	String POOL_NAME;
	
	@Value("${tsys.datasource.idle-timeout}")
	String IDLE_TIMEOUT;
	
	@Value("${tsys.datasource.max-lifetime}")
	String MAX_LIFETIME;
	
	@Value("${tsys.datasource.connection-timeout}")
	String CONNECTION_TIMEOUT;
	
	@Value("${tsys.datasource.transaction-isolation}")
	String ISOLATION_LEVEL;
	
	@Value("${tsys.datasource.leak-detection-threshold}")
	String LEAK_DETCTION_THRESHOLD;	
	
	@Bean(name = "tsysDataSource")
	@Primary
	public DataSource tsysDataSource() {
		
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

	@Bean(name = "tsysTxManager")
	//@Primary
	public PlatformTransactionManager tsysTxManager(@Qualifier("tsysDataSource") DataSource tsysDataSource) {
		return new DataSourceTransactionManager(tsysDataSource);
	}

	@Bean(name = "tsysSqlSessionFactory")
	@Primary
	public SqlSessionFactory tsysSqlSessionFactory(@Qualifier("tsysDataSource") DataSource tsysDataSource, ApplicationContext applicationContext) throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(tsysDataSource);
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:com/sci4s/msa/mapper/tsys/Mapper.xml"));
		
		return factoryBean.getObject();
	}

	@Bean(name = "tsysSqlSessionTemplate", destroyMethod = "clearCache")
	@Primary
	public SqlSessionTemplate tsysSqlSessionTemplate(SqlSessionFactory tsysSqlSessionFactory) {
		return new SqlSessionTemplate(tsysSqlSessionFactory);
	}
}
