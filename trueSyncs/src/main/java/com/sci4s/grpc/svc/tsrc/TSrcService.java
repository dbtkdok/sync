package com.sci4s.grpc.svc.tsrc;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sci4s.msa.mapper.tsrc.TSrcMapper;

@Service
public class TSrcService {
	
	Logger logger = LoggerFactory.getLogger(TSrcService.class);
	
	@Value("${tsrc.db.dbtype}")
	String SQLMODE;
	
	@Value("${tsrc.default.lang}")
	String CLANG;
	
	TSrcMapper tsrcMapper;
	
	@Autowired
	public TSrcService(TSrcMapper tsrcMapper){
	    this.tsrcMapper = tsrcMapper;
	}
	
	/**
	 * 공통코드 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblCodesAll(List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("tsrcMapper.saveTblCustInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tsrcMapper.saveTblCodesAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if(rsInfo.get("flag").equals("PRO")) {
				if (tsrcMapper.existsTblCodes(rsInfo) == 0) {
					tsrcMapper.insTblCodes(rsInfo);
				} else {
					tsrcMapper.updTblCodes(rsInfo);
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
		logger.debug("tscmMapper.saveTblBorgsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tscmMapper.saveTblBorgsAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tsrcMapper.existsTblBorgs(rsInfo) == 0) {
				tsrcMapper.insTblBorgs(rsInfo);
			} else {
				tsrcMapper.updTblBorgs(rsInfo);
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
		logger.debug("tsrcMapper.saveTblCustInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tsrcMapper.saveTblCustInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tsrcMapper.existsTblCustInfo(rsInfo) == 0) {
				tsrcMapper.insTblCustInfo(rsInfo);
			} else {
				tsrcMapper.updTblCustInfo(rsInfo);
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
		logger.debug("tsrcMapper.saveTblVdInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tsrcMapper.saveTblVdInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tsrcMapper.existsTblVdInfo(rsInfo) == 0) {
				tsrcMapper.insTblVdInfo(rsInfo);
			} else {
				tsrcMapper.updTblVdInfo(rsInfo);
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
		logger.debug("tsrcMapper.saveTblCustVdInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tsrcMapper.saveTblCustVdInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tsrcMapper.existsTblCustVdInfo(rsInfo) == 0) {
				tsrcMapper.insTblCustVdInfo(rsInfo);
			} else {
				tsrcMapper.updTblCustVdInfo(rsInfo);
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
		logger.debug("tsrcMapper.saveTblVdPartnersAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tsrcMapper.saveTblVdPartnersAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tsrcMapper.existsTblVdPartners(rsInfo) == 0) {
				tsrcMapper.insTblVdPartners(rsInfo);
			} else {
				tsrcMapper.updTblVdPartners(rsInfo);
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
		logger.debug("tsrcMapper.saveTblUserInfoAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tsrcMapper.saveTblUserInfoAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tsrcMapper.existsTblUserInfo(rsInfo) == 0) {
				tsrcMapper.insTblUserInfo(rsInfo);
			} else {
				tsrcMapper.updTblUserInfo(rsInfo);
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
		logger.debug("tsrcMapper.saveTblUserCustAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tsrcMapper.saveTblUserCustAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tsrcMapper.existsTblUserCust(rsInfo) == 0) {
				tsrcMapper.insTblUserCust(rsInfo);
			} else {
				tsrcMapper.updTblUserCust(rsInfo);
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
		logger.debug("tsrcMapper.saveTblCustItemsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			//logger.debug("tsrcMapper.saveTblCustItemsAll.rsInfo.codeID ::: "+ rsInfo.get("codeID"));
			if (tsrcMapper.existsTblCustItems(rsInfo) == 0) {
				tsrcMapper.insTblCustItems(rsInfo);
			} else {
				tsrcMapper.updTblCustItems(rsInfo);
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
		logger.debug("tsrcMapper.saveTblItemsAll run="+ rsList.size());
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			rsInfo.put("clang", this.CLANG);
			
			if (tsrcMapper.existsTblItems(rsInfo) == 0) {
				tsrcMapper.insTblItems(rsInfo);
			} else {
				tsrcMapper.updTblItems(rsInfo);
			}
		}
	}
}
