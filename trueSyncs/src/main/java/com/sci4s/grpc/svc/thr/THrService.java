package com.sci4s.grpc.svc.thr;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sci4s.msa.mapper.thr.THrMapper;

@Service
public class THrService {
	
	Logger logger = LoggerFactory.getLogger(THrService.class);
	
	@Value("${thr.db.dbtype}")
	String SQLMODE;
	
	@Value("${thr.default.lang}")
	String CLANG;
	
	THrMapper thrMapper;
	
	@Autowired
	public THrService(THrMapper thrMapper){
	    this.thrMapper = thrMapper;
	}
	
	/**
	 * 공통코드 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblCodesAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("thrMapper.saveTblCustInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("thrMapper.saveTblCodesAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if(rsInfo.get("flag").equals("PRO")) {
				if (thrMapper.existsTblCodes(rsInfo) == 0) {
					thrMapper.insTblCodes(rsInfo);
				} else {
					thrMapper.updTblCodes(rsInfo);
				}
			}
		}
	}

}
