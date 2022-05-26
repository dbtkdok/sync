package com.sci4s.msa.mapper.tcims;

import java.util.Map;

public interface TCimsMapper {
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
	
	/* 매핑 품목 싱크용 */
	public int existsTblGpnItems(Map<String,Object> param) throws Exception;
	public int insTblGpnItems(Map<String,Object> param) throws Exception;
	public int updTblGpnItems(Map<String,Object> param) throws Exception;
	
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
