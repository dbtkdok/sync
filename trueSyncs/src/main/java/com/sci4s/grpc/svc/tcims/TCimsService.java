package com.sci4s.grpc.svc.tcims;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sci4s.grpc.svc.tscm.TScmService;
import com.sci4s.msa.mapper.tcims.TCimsMapper;

@Service
public class TCimsService {
	
	Logger logger = LoggerFactory.getLogger(TScmService.class);
	
	
	@Value("${tscm.db.dbtype}")
	String SQLMODE;
	
	@Value("${tscm.default.lang}")
	String CLANG;
	
	TCimsMapper  tcimsMapper;
	
	@Autowired
	public TCimsService(TCimsMapper tcimsMapper){
	    this.tcimsMapper = tcimsMapper;
	}
	
	/**
	 * 공통코드 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblCodesAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tcimsMapper.saveTblCustInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcimsMapper.saveTblCodesAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if(rsInfo.get("flag").equals("PRO")) {
				if (tcimsMapper.existsTblCodes(rsInfo) == 0) {
					tcimsMapper.insTblCodes(rsInfo);
				} else {
					tcimsMapper.updTblCodes(rsInfo);
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
		logger.debug("tcimsMapper.saveTblBorgsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcimsMapper.saveTblBorgsAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcimsMapper.existsTblBorgs(rsInfo) == 0) {
				tcimsMapper.insTblBorgs(rsInfo);
			} else {
				tcimsMapper.updTblBorgs(rsInfo);
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
		logger.debug("tcimsMapper.saveTblCustInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcimsMapper.saveTblCustInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcimsMapper.existsTblCustInfo(rsInfo) == 0) {
				tcimsMapper.insTblCustInfo(rsInfo);
			} else {
				tcimsMapper.updTblCustInfo(rsInfo);
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
		logger.debug("tcimsMapper.saveTblVdInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcimsMapper.saveTblVdInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcimsMapper.existsTblVdInfo(rsInfo) == 0) {
				tcimsMapper.insTblVdInfo(rsInfo);
			} else {
				tcimsMapper.updTblVdInfo(rsInfo);
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
		logger.debug("tcimsMapper.saveTblCustVdInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcimsMapper.saveTblCustInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcimsMapper.existsTblCustVdInfo(rsInfo) == 0) {
				tcimsMapper.insTblCustVdInfo(rsInfo);
			} else {
				tcimsMapper.updTblCustVdInfo(rsInfo);
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
		logger.debug("tcimsMapper.saveTblVdPartnersAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcimsMapper.saveTblVdPartnersAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcimsMapper.existsTblVdPartners(rsInfo) == 0) {
				tcimsMapper.insTblVdPartners(rsInfo);
			} else {
				tcimsMapper.updTblVdPartners(rsInfo);
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
		logger.debug("tcimsMapper.saveTblUserInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcimsMapper.saveTblUserInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcimsMapper.existsTblUserInfo(rsInfo) == 0) {
				tcimsMapper.insTblUserInfo(rsInfo);
			} else {
				tcimsMapper.updTblUserInfo(rsInfo);
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
		logger.debug("tcimsMapper.saveTblUserCustAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcimsMapper.saveTblUserCustAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcimsMapper.existsTblUserCust(rsInfo) == 0) {
				tcimsMapper.insTblUserCust(rsInfo);
			} else {
				tcimsMapper.updTblUserCust(rsInfo);
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
		logger.debug("tcimsMapper.saveTblCustItemsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcimsMapper.saveTblCustItemsAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcimsMapper.existsTblCustItems(rsInfo) == 0) {
				tcimsMapper.insTblCustItems(rsInfo);
			} else {
				tcimsMapper.updTblCustItems(rsInfo);
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
		logger.debug("tcimsMapper.saveTblItemsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			
			if (tcimsMapper.existsTblItems(rsInfo) == 0) {
				tcimsMapper.insTblItems(rsInfo);
			} else {
				tcimsMapper.updTblItems(rsInfo);
			}
		}
	}
	
	/**
	 * 매핑 품목  정보 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblGpnItemsAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tcimsMapper.saveTblGpnItemsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			
			if (tcimsMapper.existsTblGpnItems(rsInfo) == 0) {
				tcimsMapper.insTblGpnItems(rsInfo);
			} else {
				tcimsMapper.updTblGpnItems(rsInfo);
			}
		}
	}
}
