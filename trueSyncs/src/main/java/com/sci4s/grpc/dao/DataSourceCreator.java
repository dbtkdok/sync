package com.sci4s.grpc.dao;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariDataSource;

public class DataSourceCreator {

	public static DataSource createHikariDataSource(Properties properties) {
    	
        HikariDataSource dataSource = new HikariDataSource();
        
        String poolName = properties.get("poolName").toString();
        String driverName = properties.get("driverClassName").toString();
        dataSource.setJdbcUrl(properties.get("url").toString());
        dataSource.setUsername(properties.get("username").toString());
        dataSource.setPassword(properties.get("password").toString());
        dataSource.setDriverClassName(driverName);
        dataSource.setMinimumIdle(Integer.parseInt(properties.get("minimum").toString()));
        dataSource.setMaximumPoolSize(Integer.parseInt(properties.get("maximumPoolSize").toString()));
        dataSource.setPoolName(poolName);
        dataSource.setIdleTimeout(Long.parseLong(properties.get("idleTimeout").toString()));
    	dataSource.setConnectionTimeout(Long.parseLong(properties.get("connectionTimeout").toString()));
        dataSource.setMaxLifetime(Long.parseLong(properties.get("maxLifetime").toString()));
        dataSource.setTransactionIsolation(properties.get("isolationLevel").toString());
    	dataSource.setLeakDetectionThreshold(Long.parseLong(properties.get("leakDetectionThreshold").toString()));

        return new LazyConnectionDataSourceProxy(dataSource);
    }
}