package com.sci4s.grpc.svc.tnote;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sci4s.msa.mapper.tnote.TNoteMapper;

@Service
public class TNoteService {
	
	Logger logger = LoggerFactory.getLogger(TNoteService.class);
	
	@Value("${tnote.db.dbtype}")
	String SQLMODE;
	
	@Value("${tnote.default.lang}")
	String CLANG;
	
	TNoteMapper tnoteMapper;
	
	@Autowired
	public  TNoteService(TNoteMapper tnoteMapper){
	    this.tnoteMapper = tnoteMapper;
	}
	
	@Transactional
	public void saveTblAttachAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblAttachAll run");
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			
			logger.debug("saveTblAttachAll.rsInfo ::: "+ rsInfo.get("attachID"));
			if (tnoteMapper.existsTblAttach(rsInfo) == 0) {
				tnoteMapper.insTblAttach(rsInfo);
			} else {
				tnoteMapper.updTblAttach(rsInfo); 
			}
		}
	}
}