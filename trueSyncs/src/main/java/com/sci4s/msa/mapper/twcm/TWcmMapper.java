package com.sci4s.msa.mapper.twcm;

import java.util.Map;

public interface TWcmMapper {
	public Map<String,Object> getUUID(Map<String,Object> param) throws Exception;
	
	/* 공통코드 싱크용 */
	public Long existsTblCodes(Map<String,Object> param) throws Exception;
	public int insTblCodes(Map<String,Object> param) throws Exception;
	public int updTblCodes(Map<String,Object> param) throws Exception;
	
}