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
@MapperScan(value = "com.sci4s.msa.mapper.tsrc", sqlSessionFactoryRef = "tsrcSqlSessionFactory")
@EnableTransactionManagement
public class TSrcSqlSessionConfig {
	
	@Value("${tsrc.datasource.url}")
	String DB_URL;
	
	@Value("${tsrc.datasource.username}")
	String DB_USER;
	
	@Value("${tsrc.datasource.password}")
	String DB_PWD;
	
	@Value("${tsrc.datasource.driverClassName}")
	String DB_DRIVER;
	
	@Value("${tsrc.datasource.hikari.minimum-idle}")
	String MINIUM;
	
	@Value("${tsrc.datasource.maximum-pool-size}")
	String MAXIUM_POOLSIZE;
	
	@Value("${tsrc.datasource.pool-name}")
	String POOL_NAME;
	
	@Value("${tsrc.datasource.idle-timeout}")
	String IDLE_TIMEOUT;
	
	@Value("${tsrc.datasource.max-lifetime}")
	String MAX_LIFETIME;
	
	@Value("${tsrc.datasource.connection-timeout}")
	String CONNECTION_TIMEOUT;
	
	@Value("${tsrc.datasource.transaction-isolation}")
	String ISOLATION_LEVEL;
	
	@Value("${tsrc.datasource.leak-detection-threshold}")
	String LEAK_DETCTION_THRESHOLD;	
	
	@Bean(name = "tsrcDataSource")
	public DataSource tsrcDataSource() {
		
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

	@Bean(name = "tsrcTxManager")
	public PlatformTransactionManager tsrcTxManager(@Qualifier("tsrcDataSource") DataSource tsrcDataSource) {
		return new DataSourceTransactionManager(tsrcDataSource);
	}

	@Bean(name = "tsrcSqlSessionFactory")
	public SqlSessionFactory tsrcSqlSessionFactory(@Qualifier("tsrcDataSource") DataSource tsrcDataSource, ApplicationContext applicationContext) throws Exception {

		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(tsrcDataSource);
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:com/sci4s/msa/mapper/tsrc/Mapper.xml"));

		return factoryBean.getObject();
	}

	@Bean(name = "tsrcSqlSessionTemplate", destroyMethod = "clearCache")
	public SqlSessionTemplate tsrcSqlSessionTemplate(SqlSessionFactory tsrcSqlSessionFactory) {
		return new SqlSessionTemplate(tsrcSqlSessionFactory);
	}

	/**
	@Bean
	public MapperFactoryBean<TSrcMapper> tsrcMapper(SqlSessionFactory tsrcSqlSessionFactory) {

		MapperFactoryBean<TSrcMapper> factoryBean = new MapperFactoryBean<>(TSrcMapper.class);
		factoryBean.setSqlSessionFactory(tsrcSqlSessionFactory);
		return factoryBean;
	}
	*/
}
