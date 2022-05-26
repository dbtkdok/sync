package com.sci4s.grpc.svc.tscm;

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

import com.sci4s.msa.mapper.tscm.TScmMapper;

@Service
public class TScmService {
	
	Logger logger = LoggerFactory.getLogger(TScmService.class);
	
	
	@Value("${tscm.db.dbtype}")
	String SQLMODE;
	
	@Value("${tscm.default.lang}")
	String CLANG;
	
	TScmMapper  tscmMapper;
	
	@Autowired
	public TScmService(TScmMapper tscmMapper){
	    this.tscmMapper = tscmMapper;
	}
	
	/**
	 * 공통코드 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblCodesAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblCustInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblCodesAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if(rsInfo.get("flag").equals("PRO")) {
				if (tscmMapper.existsTblCodes(rsInfo) == 0) {
					tscmMapper.insTblCodes(rsInfo);
				} else {
					tscmMapper.updTblCodes(rsInfo);
				}
			}
		}
		//int ii = 1000/0;
	}
	
	/**
	 * 부서정보 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblBorgsAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblBorgsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblBorgsAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tscmMapper.existsTblBorgs(rsInfo) == 0) {
				tscmMapper.insTblBorgs(rsInfo);
			} else {
				tscmMapper.updTblBorgs(rsInfo);
			}
		}
	}
	
	/**
	 * 고객사정보 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblCustInfoAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblCustInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblCustInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tscmMapper.existsTblCustInfo(rsInfo) == 0) {
				tscmMapper.insTblCustInfo(rsInfo);
			} else {
				tscmMapper.updTblCustInfo(rsInfo);
			}
		}
	}
	
	/**
	 * 협력사정보 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblVdInfoAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblVdInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblVdInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tscmMapper.existsTblVdInfo(rsInfo) == 0) {
				tscmMapper.insTblVdInfo(rsInfo);
			} else {
				tscmMapper.updTblVdInfo(rsInfo);
			}
		}
	}
	
	/**
	 * 고객협력사정보 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblCustVdInfoAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblCustVdInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblCustInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tscmMapper.existsTblCustVdInfo(rsInfo) == 0) {
				tscmMapper.insTblCustVdInfo(rsInfo);
			} else {
				tscmMapper.updTblCustVdInfo(rsInfo);
			}
		}
	}
	
	/**
	 * 협력사 거래처 마스터 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblVdPartnersAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblVdPartnersAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblVdPartnersAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tscmMapper.existsTblVdPartners(rsInfo) == 0) {
				tscmMapper.insTblVdPartners(rsInfo);
			} else {
				tscmMapper.updTblVdPartners(rsInfo);
			}
		}
	}
	
	/**
	 * 사용자 마스터 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblUserInfoAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblUserInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblUserInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tscmMapper.existsTblUserInfo(rsInfo) == 0) {
				tscmMapper.insTblUserInfo(rsInfo);
			} else {
				tscmMapper.updTblUserInfo(rsInfo);
			}
		}
	}
	
	/**
	 * 운영담당자 고객사 정보 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblUserCustAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblUserCustAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblUserCustAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tscmMapper.existsTblUserCust(rsInfo) == 0) {
				tscmMapper.insTblUserCust(rsInfo);
			} else {
				tscmMapper.updTblUserCust(rsInfo);
			}
		}
	}
		
	/**
	 * 일반계약 발주 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblPoDataAll(List<Map<String,Object>> rsList) throws Exception {
		
		Map<String,Object> parms = new HashMap<String,Object>();
		parms.put("SQLMODE", this.SQLMODE);
		parms.put("clang", this.CLANG);
		String uuID = null;
		Map<String, Object> uuIDMap = tscmMapper.getUUID(parms);
		if (uuIDMap != null) {
			uuID = ""+ uuIDMap.get("uuID");
			parms.put("uuID", uuID);
		}
		logger.debug("saveTblPoDataAll run");
		List<String> poList = new ArrayList<String>();
		List<Map<String,Object>> poMstList = new ArrayList<Map<String,Object>>();
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("uuID", uuID);			
			String poDocID = ""+ rsInfo.get("poDocID");
			
			logger.debug("saveTblPoDataAll.rsInfo.uuID ::: "+ rsInfo.get("uuID"));
			if (tscmMapper.existsTblPoDetail4IF(rsInfo) == 0) {
				tscmMapper.insTblPoDetail4IF(rsInfo);
			} else {
				tscmMapper.updTblPoDetail4IF(rsInfo);
			}
			
			if (tscmMapper.existsTblPoAdpt4IF(rsInfo) == 0) {
				tscmMapper.insTblPoAdpt4IF(rsInfo);
			} else {
				tscmMapper.updTblPoAdpt4IF(rsInfo);
			}
			
			if (poList.contains(poDocID)) {
				
			} else {
				poMstList.add(rsInfo);
				poList.add(poDocID);
			}
		}
		for (Map<String,Object> rsInfo :poMstList) {	
			String poDocID = ""+ rsInfo.get("poDocID");
			String poDtlID = ""+ rsInfo.get("poDtlID");
			logger.info("poDocID:::::::::::"+ poDocID);
			parms.put("poDocID", poDocID);
			parms.put("poDtlID", poDtlID);
			if (tscmMapper.existsTblPoMaster4IF(parms) == 0) {
				tscmMapper.insTblPoMaster4IF(parms);
			} else {
				tscmMapper.updTblPoMaster4IF(rsInfo);
			}
		}
	}
	
	/**
	 * 고객사 품목  정보 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblCustItemsAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblCustItemsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblCustItemsAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tscmMapper.existsTblCustItems(rsInfo) == 0) {
				tscmMapper.insTblCustItems(rsInfo);
			} else {
				tscmMapper.updTblCustItems(rsInfo);
			}
		}
	}
	
	/**
	 * 품목  정보 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblItemsAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tscmMapper.saveTblItemsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			
			if (tscmMapper.existsTblItems(rsInfo) == 0) {
				tscmMapper.insTblItems(rsInfo);
			} else {
				tscmMapper.updTblItems(rsInfo);
			}
		}
	}
}
