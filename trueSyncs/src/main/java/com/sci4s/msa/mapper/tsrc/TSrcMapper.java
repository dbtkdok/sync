package com.sci4s.msa.mapper.tsrc;

import java.util.Map;

public interface TSrcMapper {
	public Map<String,Object> getUUID(Map<String,Object> param) throws Exception;
	
	/* 공통코드 싱크용 */
	public int existsTblCodes(Map<String,Object> param) throws Exception;
	public int insTblCodes(Map<String,Object> param) throws Exception;
	public int updTblCodes(Map<String,Object> param) throws Exception;
	
	/* 2. 사용자  싱크용 */
	public int existsTblUserInfo(Map<String,Object> param) throws Exception;
	public int insTblUserInfo(Map<String,Object> param) throws Exception;
	public int updTblUserInfo(Map<String,Object> param) throws Exception;
	
	/* 3. 부서  싱크용 */
	public int existsTblBorgs(Map<String,Object> param) throws Exception;
	public int insTblBorgs(Map<String,Object> param) throws Exception;
	public int updTblBorgs(Map<String,Object> param) throws Exception;
	
	/* 4. 고객사  싱크용 */
	public int existsTblCustInfo(Map<String,Object> param) throws Exception;
	public int insTblCustInfo(Map<String,Object> param) throws Exception;
	public int updTblCustInfo(Map<String,Object> param) throws Exception;
	
	/* 5. 고객협력사 싱크용 */
	public int existsTblCustVdInfo(Map<String,Object> param) throws Exception;
	public int insTblCustVdInfo(Map<String,Object> param) throws Exception;
	public int updTblCustVdInfo(Map<String,Object> param) throws Exception;
	
	/* 6. 공급사  싱크용 */
	public int existsTblVdInfo(Map<String,Object> param) throws Exception;
	public int insTblVdInfo(Map<String,Object> param) throws Exception;
	public int updTblVdInfo(Map<String,Object> param) throws Exception;
	
	/* 7. 협력사 거래처 마스터 싱크용 */
	public int existsTblVdPartners(Map<String,Object> param) throws Exception;
	public int insTblVdPartners(Map<String,Object> param) throws Exception;
	public int updTblVdPartners(Map<String,Object> param) throws Exception;
	
	/* 8. 고객사품목  싱크용 */
	public int existsTblCustItems(Map<String,Object> param) throws Exception;
	public int insTblCustItems(Map<String,Object> param) throws Exception;
	public int updTblCustItems(Map<String,Object> param) throws Exception;
	
	/* 9. 품목마스터  싱크용 */
	public int existsTblItems(Map<String,Object> param) throws Exception;
	public int insTblItems(Map<String,Object> param) throws Exception;
	public int updTblItems(Map<String,Object> param) throws Exception;
	
	/* 10. 운영담당자 고객사 정보 싱크용 */
	public int existsTblUserCust(Map<String,Object> param) throws Exception;
	public int insTblUserCust(Map<String,Object> param) throws Exception;
	public int updTblUserCust(Map<String,Object> param) throws Exception;
}