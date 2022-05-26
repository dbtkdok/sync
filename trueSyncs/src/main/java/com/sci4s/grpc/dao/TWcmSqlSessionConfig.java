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
@MapperScan(value = "com.sci4s.msa.mapper.twcm", sqlSessionFactoryRef = "twcmSqlSessionFactory")
@EnableTransactionManagement
public class TWcmSqlSessionConfig {
	
	@Value("${twcm.datasource.url}")
	String DB_URL;
	
	@Value("${twcm.datasource.username}")
	String DB_USER;
	
	@Value("${twcm.datasource.password}")
	String DB_PWD;
	
	@Value("${twcm.datasource.driverClassName}")
	String DB_DRIVER;
	
	@Value("${twcm.datasource.hikari.minimum-idle}")
	String MINIUM;
	
	@Value("${twcm.datasource.maximum-pool-size}")
	String MAXIUM_POOLSIZE;
	
	@Value("${twcm.datasource.pool-name}")
	String POOL_NAME;
	
	@Value("${twcm.datasource.idle-timeout}")
	String IDLE_TIMEOUT;
	
	@Value("${twcm.datasource.max-lifetime}")
	String MAX_LIFETIME;
	
	@Value("${twcm.datasource.connection-timeout}")
	String CONNECTION_TIMEOUT;
	
	@Value("${twcm.datasource.transaction-isolation}")
	String ISOLATION_LEVEL;
	
	@Value("${twcm.datasource.leak-detection-threshold}")
	String LEAK_DETCTION_THRESHOLD;	
	
	@Bean(name = "twcmDataSource")
	public DataSource twcmDataSource() {
		
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

	@Bean(name = "twcmTxManager")
	public PlatformTransactionManager twcmTxManager(@Qualifier("twcmDataSource") DataSource twcmDataSource) {
		return new DataSourceTransactionManager(twcmDataSource);
	}

	@Bean(name = "twcmSqlSessionFactory")
	public SqlSessionFactory twcmSqlSessionFactory(@Qualifier("twcmDataSource") DataSource twcmDataSource, ApplicationContext applicationContext) throws Exception {

		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(twcmDataSource);
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:com/sci4s/msa/mapper/twcm/Mapper.xml"));

		return factoryBean.getObject();
	}
	
	@Bean(name = "twcmSqlSessionTemplate", destroyMethod = "clearCache")
	public SqlSessionTemplate twcmSqlSessionTemplate(SqlSessionFactory twcmSqlSessionFactory) {
		return new SqlSessionTemplate(twcmSqlSessionFactory);
	}
}
