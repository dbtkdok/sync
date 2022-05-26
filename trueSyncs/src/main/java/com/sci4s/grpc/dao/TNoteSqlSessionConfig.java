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
@MapperScan(value = "com.sci4s.msa.mapper.tnote", sqlSessionFactoryRef = "tnoteSqlSessionFactory")
@EnableTransactionManagement
public class TNoteSqlSessionConfig {
	
	@Value("${tnote.datasource.url}")
	String DB_URL;
	
	@Value("${tnote.datasource.username}")
	String DB_USER;
	
	@Value("${tnote.datasource.password}")
	String DB_PWD;
	
	@Value("${tnote.datasource.driverClassName}")
	String DB_DRIVER;
	
	@Value("${tnote.datasource.hikari.minimum-idle}")
	String MINIUM;
	
	@Value("${tnote.datasource.maximum-pool-size}")
	String MAXIUM_POOLSIZE;
	
	@Value("${tnote.datasource.pool-name}")
	String POOL_NAME;
	
	@Value("${tnote.datasource.idle-timeout}")
	String IDLE_TIMEOUT;
	
	@Value("${tnote.datasource.max-lifetime}")
	String MAX_LIFETIME;
	
	@Value("${tnote.datasource.connection-timeout}")
	String CONNECTION_TIMEOUT;
	
	@Value("${tnote.datasource.transaction-isolation}")
	String ISOLATION_LEVEL;
	
	@Value("${tnote.datasource.leak-detection-threshold}")
	String LEAK_DETCTION_THRESHOLD;	
	
	@Bean(name = "tnoteDataSource")
	public DataSource tnoteDataSource() {
		
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

	@Bean(name = "tnoteTxManager")
	public PlatformTransactionManager tnoteTxManager(@Qualifier("tnoteDataSource") DataSource tnoteDataSource) {
		return new DataSourceTransactionManager(tnoteDataSource);
	}

	@Bean(name = "tnoteSqlSessionFactory")
	public SqlSessionFactory tnoteSqlSessionFactory(@Qualifier("tnoteDataSource") DataSource tnoteDataSource, ApplicationContext applicationContext) throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(tnoteDataSource);
		factoryBean.setMapperLocations(applicationContext.getResources("classpath:com/sci4s/msa/mapper/tnote/Mapper.xml"));
		
		return factoryBean.getObject();
	}

	@Bean(name = "tnoteSqlSessionTemplate", destroyMethod = "clearCache")
	public SqlSessionTemplate tnoteSqlSessionTemplate(SqlSessionFactory tnoteSqlSessionFactory) {
		return new SqlSessionTemplate(tnoteSqlSessionFactory);
	}
}
