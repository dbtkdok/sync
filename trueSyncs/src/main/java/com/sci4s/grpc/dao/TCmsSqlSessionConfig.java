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
@MapperScan(value = "com.sci4s.msa.mapper.tcms", sqlSessionFactoryRef = "tcmsSqlSessionFactory")
@EnableTransactionManagement
public class TCmsSqlSessionConfig {
	
	@Value("${tcms.datasource.url}")
	String DB_URL;
	
	@Value("${tcms.datasource.username}")
	String DB_USER;
	
	@Value("${tcms.datasource.password}")
	String DB_PWD;
	
	@Value("${tcms.datasource.driverClassName}")
	String DB_DRIVER;
	
	@Value("${tcms.datasource.hikari.minimum-idle}")
	String MINIUM;
	
	@Value("${tcms.datasource.maximum-pool-size}")
	String MAXIUM_POOLSIZE;
	
	@Value("${tcms.datasource.pool-name}")
	String POOL_NAME;
	
	@Value("${tcms.datasource.idle-timeout}")
	String IDLE_TIMEOUT;
	
	@Value("${tcms.datasource.max-lifetime}")
	String MAX_LIFETIME;
	
	@Value("${tcms.datasource.connection-timeout}")
	String CONNECTION_TIMEOUT;
	
	@Value("${tcms.datasource.transaction-isolation}")
	String ISOLATION_LEVEL;
	
	@Value("${tcms.datasource.leak-detection-threshold}")
	String LEAK_DETCTION_THRESHOLD;	
	
	@Bean(name = "tcmsDataSource")
	public DataSource tcmsDataSource() {
		
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

	@Bean(name = "tcmsTxManager")
	public PlatformTransactionManager tcmsTxManager(@Qualifier("tcmsDataSource") DataSource tcmsDataSource) {
		return new DataSourceTransactionManager(tcmsDataSource);
	}

	@Bean(name = "tcmsSqlSessionFactory")
	public SqlSessionFactory tcmsSqlSessionFactory(@Qualifier("tcmsDataSource") DataSource tcmsDataSource, ApplicationContext applicationContext) throws Exception {

		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(tcmsDataSource);
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:com/sci4s/msa/mapper/tcms/Mapper.xml"));

		return factoryBean.getObject();
	}
	
	@Bean(name = "tcmsSqlSessionTemplate", destroyMethod = "clearCache")
	public SqlSessionTemplate tcmsSqlSessionTemplate(SqlSessionFactory tcmsSqlSessionFactory) {
		return new SqlSessionTemplate(tcmsSqlSessionFactory);
	}
}
