package com.sci4s.msa.mapper.tsys;

import java.util.List;
import java.util.Map;

public interface TSysMapper {
	public Map<String,Object> getUUID(Map<String,Object> param) throws Exception;
	/*
	 * privID xml 생성 쿼리
	 */
	public List<Map<String,Object>> getEnableJobCache(Map<String,Object> param) throws Exception;
	public int getTblUserRoleMapCnt() throws Exception;
	public int updTblUserRoleMap() throws Exception;
	public int insTblUserRoleMap(Map<String,Object> param) throws Exception;
	/*
	 * 메뉴 xml 생성 쿼리
	 */
	public List<Map<String,Object>> getTblSyncDictionary(Map<String,Object> param) throws Exception;
	public List<Map<String,Object>> getTblDicList(Map<String,Object> param) throws Exception;
	public int updTblSyncDictionary(Map<String,Object> param) throws Exception;
	
	/*
	 * 메뉴 xml 생성 쿼리
	 */
	public List<Map<String,Object>> getTblSyncMenu(Map<String,Object> param) throws Exception;
	public List<Map<String,Object>> getTblTopMenuList(Map<String,Object> param) throws Exception;
	public List<Map<String,Object>> getTblLeftMenuList(Map<String,Object> param) throws Exception;
	public int updTblSyncMenu(Map<String,Object> param) throws Exception;
	
	/*
	 * json 파일 생성 쿼리
	 */
	public List<Map<String,Object>> getTblSyncJobHist(Map<String,Object> param) throws Exception;
	public int insTblSyncJobHist(Map<String,Object> param) throws Exception;
	public int updTblSyncJobHist(Map<String,Object> param) throws Exception;
	public int updTblSyncJobHist4IfPSTS(Map<String,Object> param) throws Exception;
	public int updTblSyncJobHist4Bak(Map<String,Object> param) throws Exception;
	public int insTblSyncJobHist4Bak(Map<String,Object> param) throws Exception;
	public int delTblSyncJobHist4Bak(Map<String,Object> param) throws Exception;
	
	/*
	 * 싱크 처리 쿼리
	 */	
	public int insTblSyncJobRequest(List<Map<String, Object>> param) throws Exception;
	public int updTblSyncJobRequest4IfPSTS(Map<String,Object> param) throws Exception;
	public List<Map<String,Object>> getTblSyncJobRequest(Map<String,Object> param) throws Exception;
	public int updTblSyncJobRequest(Map<String,Object> param) throws Exception;
	
	
	/* 사용자 권한  싱크용 */
	public int existsTblUserInfo(Map<String,Object> param) throws Exception;
	public int insTblUserInfo(Map<String,Object> param) throws Exception;
	public int updTblUserInfo(Map<String,Object> param) throws Exception;
	
	/* 사용자별 권한  싱크용 */
	public int existsTblUserPrivs(Map<String,Object> param) throws Exception;
	public int mergeTblUserPrivs4Mariadb(Map<String,Object> param) throws Exception;
	public int mergeTblUserPrivs4Oracle(Map<String,Object> param) throws Exception;
	public int insTblUserPrivs(Map<String,Object> param) throws Exception;
	public int updTblUserPrivs(Map<String,Object> param) throws Exception;
	public int delTblUserPrivs(Map<String,Object> param) throws Exception;
	
	public List<Map<String,Object>> getTblSyncJobList(Map<String,Object> param) throws Exception;	

	public int mergeTblUserInfo4Mariadb(Map<String,Object> param) throws Exception;
	public int mergeTblUserInfo4Oracle(Map<String,Object> param) throws Exception;	
	public int insTblUserPrivsHist(Map<String,Object> param) throws Exception;
	public int delTblUserPrivs4User(Map<String,Object> param) throws Exception;
	public int mergeTblUserAuth4Mariadb(Map<String,Object> param) throws Exception;
	public int mergeTblUserAuth4Oracle(Map<String,Object> param) throws Exception;
}