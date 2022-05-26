package com.sci4s.grpc.svc.tcms;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sci4s.msa.mapper.tcms.TCmsMapper;

@Service
public class TCmsService {
	
	Logger logger = LoggerFactory.getLogger(TCmsService.class);
	
	@Value("${tcms.db.dbtype}")
	String SQLMODE;
	
	@Value("${tcms.default.lang}")
	String CLANG;
	
	TCmsMapper  tcmsMapper;
	
	@Autowired
	public TCmsService(TCmsMapper tcmsMapper){
	    this.tcmsMapper = tcmsMapper;
	}	
	
	/**
	 * 공통코드 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblCodesAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tcmsMapper.saveTblCustInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcmsMapper.saveTblCodesAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if(rsInfo.get("flag").equals("PRO")) {
				if (tcmsMapper.existsTblCodes(rsInfo) == 0) {
					tcmsMapper.insTblCodes(rsInfo);
				} else {
					tcmsMapper.updTblCodes(rsInfo);
				}
			}
		}
	}
	
	/**
	 * 부서정보 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblBorgsAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tcmsMapper.saveTblBorgsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcmsMapper.saveTblBorgsAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcmsMapper.existsTblBorgs(rsInfo) == 0) {
				tcmsMapper.insTblBorgs(rsInfo);
			} else {
				tcmsMapper.updTblBorgs(rsInfo);
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
		logger.debug("tcmsMapper.saveTblCustInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcmsMapper.saveTblCustInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcmsMapper.existsTblCustInfo(rsInfo) == 0) {
				tcmsMapper.insTblCustInfo(rsInfo);
			} else {
				tcmsMapper.updTblCustInfo(rsInfo);
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
		logger.debug("tcmsMapper.saveTblVdInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcmsMapper.saveTblVdInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcmsMapper.existsTblVdInfo(rsInfo) == 0) {
				tcmsMapper.insTblVdInfo(rsInfo);
			} else {
				tcmsMapper.updTblVdInfo(rsInfo);
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
		logger.debug("tcmsMapper.saveTblCustVdInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcmsMapper.saveTblCustVdInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcmsMapper.existsTblCustVdInfo(rsInfo) == 0) {
				tcmsMapper.insTblCustVdInfo(rsInfo);
			} else {
				tcmsMapper.updTblCustVdInfo(rsInfo);
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
		logger.debug("tcmsMapper.saveTblVdPartnersAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcmsMapper.saveTblVdPartnersAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcmsMapper.existsTblVdPartners(rsInfo) == 0) {
				tcmsMapper.insTblVdPartners(rsInfo);
			} else {
				tcmsMapper.updTblVdPartners(rsInfo);
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
		logger.debug("tcmsMapper.saveTblUserInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcmsMapper.saveTblUserInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcmsMapper.existsTblUserInfo(rsInfo) == 0) {
				tcmsMapper.insTblUserInfo(rsInfo);
			} else {
				tcmsMapper.updTblUserInfo(rsInfo);
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
		logger.debug("tcmsMapper.saveTblUserCustAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tcmsMapper.saveTblUserCustAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tcmsMapper.existsTblUserCust(rsInfo) == 0) {
				tcmsMapper.insTblUserCust(rsInfo);
			} else {
				tcmsMapper.updTblUserCust(rsInfo);
			}
		}
	}
	
}
