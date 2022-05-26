package com.sci4s.grpc.svc.tsys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sci4s.grpc.svc.tsrc.TSrcService;
import com.sci4s.msa.mapper.tsys.TSysMapper;
import com.sci4s.util.XMLUtils;

@Service
public class TSysService {
	
	Logger logger = LoggerFactory.getLogger(TSrcService.class);
	
	@Value("${tsys.db.dbtype}")
	String SQLMODE;
	
	@Value("${tsys.default.lang}")
	String CLANG;
	
	TSysMapper tsysMapper;
	
	@Autowired
	public TSysService(TSysMapper tsysMapper){
	    this.tsysMapper = tsysMapper;
	}
	
	/**
	 * 인터페이스 PID별 요청 데이터 생성 메서드
	 * 
	 * @param  Map<String,Object> rsMap
	 * @return long
	 * @throws Exception
	 */
	@Transactional
	public long insTblSyncJobHist(Map<String,Object> rsMap) throws Exception {		
		rsMap.put("SQLMODE", this.SQLMODE);
		rsMap.put("clang", this.CLANG);	
		
		return tsysMapper.insTblSyncJobHist(rsMap);
	}
	
	public void saveRoleMapXml(Map<String,Object> rsMap) throws Exception {	
		
		int cnt = tsysMapper.getTblUserRoleMapCnt();
		if(cnt > 0) {
			tsysMapper.updTblUserRoleMap();
			
			List<Map<String,Object>> paramList = tsysMapper.getEnableJobCache(rsMap);
			
			XMLUtils.getRoleMapXml(paramList, "rolemap", (String) rsMap.get("pathNM"));
		}
	}
	
	public void saveMenuXml(Map<String,Object> rsMap) throws Exception {	
		
		List<Map<String,Object>> rsList = tsysMapper.getTblSyncMenu(rsMap);
		logger.info("rsMap ::: "+ (rsList == null));
		
		if (rsList != null && rsList.size() > 0) {
			Map<String,Object> param = (Map<String,Object>) tsysMapper.getUUID(rsMap);
        	long uuID = 0;
        	if (param.containsKey("uuID")) {
        		uuID = Long.parseLong(param.get("uuID").toString());
        		rsMap.put("uuID", uuID);       
        		
        		int affected = tsysMapper.updTblSyncMenu(rsMap);
        		if (affected > 0) {
        			for(int i=0; i<rsList.size(); i++) {
        				Map<String,Object> paramMap = rsList.get(i);
        				rsMap.put("roleUID", paramMap.get("roleUID"));
        				
        				List<Map<String,Object>> menuTopList  = tsysMapper.getTblTopMenuList(rsMap);
        				List<Map<String,Object>> menuLeftList = tsysMapper.getTblLeftMenuList(rsMap);
        				
        				if(menuTopList != null && menuTopList.size() > 0) {
        					XMLUtils.saveTopMenuXml(menuTopList, (String) paramMap.get("roleID"), null, (String) rsMap.get("pathNM"));
        				}        				
        				if(menuLeftList != null && menuLeftList.size() > 0) {
        					XMLUtils.saveLeftMenuXml(menuLeftList, (String) paramMap.get("roleID"), null, (String) rsMap.get("pathNM"));
        				}
        			}
        		}
        	}
		}
	}
	
	public void saveDictionaryXml(Map<String,Object> rsMap) throws Exception {	
		rsMap.put("SQLMODE", this.SQLMODE);
		rsMap.put("clang", this.CLANG);	
		
		String locals = "ko|en|cn";
       	String keyNames = "langKR|langEN|langCN";
       	List<Map<String,Object>> rsList = tsysMapper.getTblSyncDictionary(rsMap);
//       	logger.info("getTblRoleMenuIF ::::::::::::::: "+ rsList);
       	if (rsList != null && rsList.size() > 0) {
           	Map<String,Object> paramMap = (Map<String,Object>) tsysMapper.getUUID(rsMap);
           	long uuID = 0;
           	if (rsMap.containsKey("uuID")) {
           		uuID = Long.parseLong(rsMap.get("uuID").toString());
           		rsMap.put("uuID", uuID);
           		
           		int affected = tsysMapper.updTblSyncDictionary(rsMap);
           		if (affected > 0) {
           			Map<String, Object> retMap = new HashMap<String, Object>();
           			rsMap.put("dbSTS", "Y");
           			
           			List<Map<String,Object>> dictList = tsysMapper.getTblDicList(rsMap);
           			String[] local = locals.split("\\|");
           			String[] keyName = keyNames.split("\\|");
       				for(int ii=0; ii<local.length; ii++) {
       					String loc = local[ii];
       					String keyNM = keyName[ii];
       					
       					XMLUtils.saveDictionaryXml(dictList, keyNM, loc, null, (String) rsMap.get("pathNM"));
       				}
           		}
           	}
       	}
	}
	
	/**
	 * 인터페이스 PID별 요청 데이터 수정 메서드
	 * 
	 * @param  Map<String,Object> rsMap
	 * @return long
	 * @throws Exception
	 */
	@Transactional
	public long updTblSyncJobHist(Map<String,Object> rsMap) throws Exception {		
		rsMap.put("SQLMODE", this.SQLMODE);	
		rsMap.put("clang", this.CLANG);	
		
		return tsysMapper.updTblSyncJobHist(rsMap);
	}
	
	/**
	 * 인터페이스 대상 조회
	 * 
	 * @param  Map<String,Object> rsMap
	 * @throws Exception
	 */
	@Transactional
	public List<Map<String,Object>> getTblSyncJobHist(Map<String,Object> rsMap) throws Exception {		
		rsMap.put("SQLMODE", this.SQLMODE);	
		rsMap.put("clang", this.CLANG);	
		
		Map<String,Object> retMap = tsysMapper.getUUID(rsMap);
		rsMap.put("uuID", Long.parseLong(""+retMap.get("uuID")));		
		int affected = tsysMapper.updTblSyncJobHist4IfPSTS(rsMap);		
		
		return tsysMapper.getTblSyncJobHist(rsMap);
	}
	
	/**
	 * 인터페이스 백업
	 * 
	 * @param  Map<String,Object> rsMap
	 * @throws Exception
	 */
	@Transactional
	public void bakTblSyncJobHist(Map<String,Object> rsMap) throws Exception {		
		rsMap.put("SQLMODE", this.SQLMODE);	
		rsMap.put("clang", this.CLANG);	
		
		Map<String,Object> retMap = tsysMapper.getUUID(rsMap);
		rsMap.put("uuID", Long.parseLong(""+retMap.get("uuID")));
		
		tsysMapper.updTblSyncJobHist4Bak(rsMap);
		tsysMapper.insTblSyncJobHist4Bak(rsMap);
		tsysMapper.delTblSyncJobHist4Bak(rsMap);		
	}
	
	/**
	 * 인터페이스 PID별 요청 데이터 수정 메서드
	 * 
	 * @param  Map<String,Object> rsMap
	 * @return long
	 * @throws Exception
	 */
	@Transactional
	public long insTblSyncJobRequest(Map<String, Object> rsMap, String[] target) throws Exception  {
		List<Map<String, Object>> paramList = new ArrayList<Map<String,Object>>();
		for(int ii=0; ii<target.length; ii++) {
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.putAll(rsMap);
			obj.put("SQLMODE", this.SQLMODE);	
			obj.put("clang", this.CLANG);	
			obj.put("msaID", target[ii]);
			paramList.add(obj);
		}
		
		return tsysMapper.insTblSyncJobRequest(paramList);
	}
	
	@Transactional
	public List<Map<String,Object>> getTblSyncJobRequest(Map<String,Object> rsMap) throws Exception {		
		rsMap.put("SQLMODE", this.SQLMODE);	
		rsMap.put("clang", this.CLANG);	
		
		Map<String,Object> retMap = tsysMapper.getUUID(rsMap);
		rsMap.put("uuID", Long.parseLong(""+retMap.get("uuID")));		
		int affected = tsysMapper.updTblSyncJobRequest4IfPSTS(rsMap);		
		
		return tsysMapper.getTblSyncJobRequest(rsMap);
	}
	
	/**
	 * 인터페이스 PID별 요청 데이터 수정 메서드
	 * 
	 * @param  Map<String,Object> rsMap
	 * @return long
	 * @throws Exception
	 */
	@Transactional
	public long updTblSyncJobRequest(Map<String,Object> rsMap) throws Exception {		
		rsMap.put("SQLMODE", this.SQLMODE);	
		rsMap.put("clang", this.CLANG);	
		
		return tsysMapper.updTblSyncJobRequest(rsMap);
	}
	
	
	/**
	 * 사용자 권한 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblUserInfoAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tsysMapper.saveTblUserInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tsysMapper.saveTblUserInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tsysMapper.existsTblUserInfo(rsInfo) == 0) {
				tsysMapper.insTblUserInfo(rsInfo);
			} else {
				tsysMapper.updTblUserInfo(rsInfo);
			}
		}
	}
	
	/**
	 * 사용자별 권한 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblUserPrivsAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tsysMapper.saveTblUserPrivsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	//  -----> 권한변경 데이터 ---> 사용자별 권한에 삭제 저장
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			
			String ifSTS = ""+ rsInfo.get("ifSTS");	
			if ("D".equals(ifSTS)) {
				logger.info("tsysMapper.delTblUserPrivs");
				tsysMapper.delTblUserPrivs(rsInfo);
			} else {				
				if ("mariadb".equals(this.SQLMODE)) {
					logger.info("tsysMapper.mergeTblUserPrivs4Mariadb");						
					tsysMapper.mergeTblUserPrivs4Mariadb(rsInfo);
				} else {
					tsysMapper.mergeTblUserPrivs4Oracle(rsInfo);
				}
			}
		}
	}
	
	/**
	 * 사용자권한 변경 정보 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblUserPrivs4User(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tsysMapper.saveTblUserPrivs4User run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	//  -----> 권한변경 데이터 ---> 사용자별 권한에 삭제 저장
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			
			if ("mariadb".equals(this.SQLMODE)) {
				logger.info("tsysMapper.mergeTblUserInfo4Mariadb");						
				tsysMapper.mergeTblUserInfo4Mariadb(rsInfo);
			} else {
				tsysMapper.mergeTblUserInfo4Oracle(rsInfo);
			}

			String dbSTS = ""+ rsInfo.get("dbSTS");	
			if ("N".equals(dbSTS)) {
				logger.info("tsysMapper.delTblUserPrivs");
				tsysMapper.insTblUserPrivsHist(rsInfo);
				tsysMapper.delTblUserPrivs4User(rsInfo);
			} else {		
				if ("M".equals(dbSTS)) {
					tsysMapper.insTblUserPrivsHist(rsInfo);
					tsysMapper.delTblUserPrivs4User(rsInfo);
				}
				if ("mariadb".equals(this.SQLMODE)) {
					logger.info("tsysMapper.mergeTblUserAuth4Mariadb");						
					tsysMapper.mergeTblUserAuth4Mariadb(rsInfo);
				} else {
					tsysMapper.mergeTblUserAuth4Oracle(rsInfo);
				}
			}
		}
		
		tsysMapper.insTblUserRoleMap(rsList.get(0));
	}

	/**
	 * 각 서비스에 요청할 인터페이스 정보를 조회함.
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	public List<Map<String,Object>> getTblSyncJobList(Map<String,Object> rsMap) throws Exception {		
		return tsysMapper.getTblSyncJobList(rsMap);
	}
	
}
