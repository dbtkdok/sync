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
@MapperScan(value = "com.sci4s.msa.mapper.tscm", sqlSessionFactoryRef = "tscmSqlSessionFactory")
@EnableTransactionManagement
public class TScmSqlSessionConfig {
	
	@Value("${tscm.datasource.url}")
	String DB_URL;
	
	@Value("${tscm.datasource.username}")
	String DB_USER;
	
	@Value("${tscm.datasource.password}")
	String DB_PWD;
	
	@Value("${tscm.datasource.driverClassName}")
	String DB_DRIVER;
	
	@Value("${tscm.datasource.hikari.minimum-idle}")
	String MINIUM;
	
	@Value("${tscm.datasource.maximum-pool-size}")
	String MAXIUM_POOLSIZE;
	
	@Value("${tscm.datasource.pool-name}")
	String POOL_NAME;
	
	@Value("${tscm.datasource.idle-timeout}")
	String IDLE_TIMEOUT;
	
	@Value("${tscm.datasource.max-lifetime}")
	String MAX_LIFETIME;
	
	@Value("${tscm.datasource.connection-timeout}")
	String CONNECTION_TIMEOUT;
	
	@Value("${tscm.datasource.transaction-isolation}")
	String ISOLATION_LEVEL;
	
	@Value("${tscm.datasource.leak-detection-threshold}")
	String LEAK_DETCTION_THRESHOLD;	
	
	@Bean(name = "tscmDataSource")
	public DataSource tscmDataSource() {
		
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

	@Bean(name = "tscmTxManager")
	public PlatformTransactionManager tscmTxManager(@Qualifier("tscmDataSource") DataSource tscmDataSource) {
		return new DataSourceTransactionManager(tscmDataSource);
	}

	@Bean(name = "tscmSqlSessionFactory")
	public SqlSessionFactory tscmSqlSessionFactory(@Qualifier("tscmDataSource") DataSource tscmDataSource, ApplicationContext applicationContext) throws Exception {

		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(tscmDataSource);
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:com/sci4s/msa/mapper/tscm/Mapper.xml"));

		return factoryBean.getObject();
	}

	@Bean(name = "tscmSqlSessionTemplate", destroyMethod = "clearCache")
	public SqlSessionTemplate tscmSqlSessionTemplate(SqlSessionFactory tscmSqlSessionFactory) {
		return new SqlSessionTemplate(tscmSqlSessionFactory);
	}
}
