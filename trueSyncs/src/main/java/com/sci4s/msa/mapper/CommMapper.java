package com.sci4s.msa.mapper;

import java.util.List;
import java.util.Map;

public interface CommMapper {
	
	public List<Map<String,Object>> getMstInfoList(Map<String,Object> param) throws Exception;
	public List<Map<String,Object>> getSyncMstData(String query) throws Exception;
	public int insInfInfoHist(Map<String,Object> param) throws Exception;
	
	public int existsTblCustInfo(Map<String,Object> param) throws Exception;
	public int insTblCustInfo(Map<String,Object> param) throws Exception;
	public int updTblCustInfo(Map<String,Object> param) throws Exception;
	
	public Map<String,Object> getUUID(Map<String,Object> param) throws Exception;
}
