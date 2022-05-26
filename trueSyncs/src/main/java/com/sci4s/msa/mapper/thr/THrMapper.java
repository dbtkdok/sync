package com.sci4s.msa.mapper.thr;

import java.util.Map;

public interface THrMapper {
	public Map<String,Object> getUUID(Map<String,Object> param) throws Exception;
	
	/* 공통코드 싱크용 */
	public int existsTblCodes(Map<String,Object> param) throws Exception;
	public int insTblCodes(Map<String,Object> param) throws Exception;
	public int updTblCodes(Map<String,Object> param) throws Exception;
	
	/* Role Scope 싱크용 */
	
}
