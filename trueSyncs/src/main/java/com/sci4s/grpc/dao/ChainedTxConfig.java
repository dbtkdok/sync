package com.sci4s.grpc.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ChainedTxConfig {
	
	@Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(PlatformTransactionManager tsysTxManager
    		, PlatformTransactionManager tcmsTxManager, PlatformTransactionManager tscmTxManager
    		, PlatformTransactionManager tsrcTxManager, PlatformTransactionManager thrTxManager
    		, PlatformTransactionManager tnoteTxManager, PlatformTransactionManager twcmTxManager
    		, PlatformTransactionManager tcimsTxManager) {
        return new ChainedTransactionManager(tsysTxManager, tcmsTxManager, tscmTxManager, tsrcTxManager, thrTxManager, tnoteTxManager, tcimsTxManager, twcmTxManager);
    }
}
