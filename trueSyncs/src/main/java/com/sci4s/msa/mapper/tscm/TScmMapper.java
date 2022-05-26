package com.sci4s.msa.mapper.tscm;

import java.util.Map;

public interface TScmMapper {
	public Map<String,Object> getUUID(Map<String,Object> param) throws Exception;
	
	/* 공통코드 싱크용 */
	public int existsTblCodes(Map<String,Object> param) throws Exception;
	public int insTblCodes(Map<String,Object> param) throws Exception;
	public int updTblCodes(Map<String,Object> param) throws Exception;
	
	/* 부서  싱크용 */
	public int existsTblBorgs(Map<String,Object> param) throws Exception;
	public int insTblBorgs(Map<String,Object> param) throws Exception;
	public int updTblBorgs(Map<String,Object> param) throws Exception;
	
	/* 고객사 싱크용 */
	public int existsTblCustInfo(Map<String,Object> param) throws Exception;
	public int insTblCustInfo(Map<String,Object> param) throws Exception;
	public int updTblCustInfo(Map<String,Object> param) throws Exception;
	
	/* 고객협력사 싱크용 */
	public int existsTblCustVdInfo(Map<String,Object> param) throws Exception;
	public int insTblCustVdInfo(Map<String,Object> param) throws Exception;
	public int updTblCustVdInfo(Map<String,Object> param) throws Exception;
	
	/* 협력사 거래처 마스터 싱크용 */
	public int existsTblVdPartners(Map<String,Object> param) throws Exception;
	public int insTblVdPartners(Map<String,Object> param) throws Exception;
	public int updTblVdPartners(Map<String,Object> param) throws Exception;
	
	/* 협력사 싱크용 */
	public int existsTblVdInfo(Map<String,Object> param) throws Exception;
	public int insTblVdInfo(Map<String,Object> param) throws Exception;
	public int updTblVdInfo(Map<String,Object> param) throws Exception;
	
	/* 고객사 품목 싱크용 */
	public int existsTblCustItems(Map<String,Object> param) throws Exception;
	public int insTblCustItems(Map<String,Object> param) throws Exception;
	public int updTblCustItems(Map<String,Object> param) throws Exception;
	
	/* 일반계약 발주 싱크용 */
	public int existsTblPoDetail4IF(Map<String,Object> param) throws Exception;
	public int insTblPoDetail4IF(Map<String,Object> param) throws Exception;
	public int updTblPoDetail4IF(Map<String,Object> param) throws Exception;
	public int existsTblPoAdpt4IF(Map<String,Object> param) throws Exception;
	public int insTblPoAdpt4IF(Map<String,Object> param) throws Exception;
	public int updTblPoAdpt4IF(Map<String,Object> param) throws Exception;
	public int existsTblPoMaster4IF(Map<String,Object> param) throws Exception;
	public int insTblPoMaster4IF(Map<String,Object> param) throws Exception;
	public int updTblPoMaster4IF(Map<String,Object> param) throws Exception;
	
	/* 사용자 마스터 싱크용 */
	public int existsTblUserInfo(Map<String,Object> param) throws Exception;
	public int insTblUserInfo(Map<String,Object> param) throws Exception;
	public int updTblUserInfo(Map<String,Object> param) throws Exception;
	
	/* 운영담당자 고객사 정보 싱크용 */
	public int existsTblUserCust(Map<String,Object> param) throws Exception;
	public int insTblUserCust(Map<String,Object> param) throws Exception;
	public int updTblUserCust(Map<String,Object> param) throws Exception;
	
	/* 고객사 품목 싱크용 */
	public int existsTblItems(Map<String,Object> param) throws Exception;
	public int insTblItems(Map<String,Object> param) throws Exception;
	public int updTblItems(Map<String,Object> param) throws Exception;
}
