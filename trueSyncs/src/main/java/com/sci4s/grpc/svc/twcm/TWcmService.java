package com.sci4s.grpc.svc.twcm;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sci4s.msa.mapper.twcm.TWcmMapper;

@Service
public class TWcmService {
	
	Logger logger = LoggerFactory.getLogger(TWcmService.class);
	
	@Value("${twcm.db.dbtype}")
	String SQLMODE;
	
	@Value("${twcm.default.lang}")
	String CLANG;
	
	TWcmMapper twcmMapper;
	
	@Autowired
	public TWcmService(TWcmMapper twcmMapper){
	    this.twcmMapper = twcmMapper;
	}
	
	public void saveTwcmCodesAll(List<Map<String,Object>> rsList) throws Exception {
		for (Map<String,Object> rsInfo :rsList) {	
//			logger.info("######################### saveTwcmCodesAll ::::: START #########################");
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcmsMapper.saveTblCodesAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if(rsInfo.get("flag").equals("WMS")) {
				Long uuid = twcmMapper.existsTblCodes(rsInfo);
				if (uuid == null) {
					twcmMapper.insTblCodes(rsInfo);
				} else {
					rsInfo.put("codeuid", uuid);
					logger.info("######################### rsInfo ::::: " + rsInfo +"#########################");
					twcmMapper.updTblCodes(rsInfo);
				}
			}
//			logger.info("######################### saveTwcmCodesAll ::::: END #########################");
		}
	}
}
