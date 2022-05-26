package com.sci4s.grpc.svc;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sci4s.grpc.svc.tcims.TCimsService;
import com.sci4s.grpc.svc.tcms.TCmsService;
import com.sci4s.grpc.svc.thr.THrService;
import com.sci4s.grpc.svc.tnote.TNoteService;
import com.sci4s.grpc.svc.tscm.TScmService;
import com.sci4s.grpc.svc.tsrc.TSrcService;
import com.sci4s.grpc.svc.tsys.TSysService;
import com.sci4s.grpc.svc.twcm.TWcmService;

@Service
public class TSyncService {
	Logger logger = LoggerFactory.getLogger(TSyncService.class);
	
	ApplicationContext appContext;
	TScmService  tscmService;
	TCmsService  tcmsService;
	TSrcService  tsrcService;
	THrService   thrService;
	TSysService  tsysService;
	TNoteService tnoteService;
	TCimsService tcimsService;
	TWcmService  twcmService;
	
	@Autowired
	public  TSyncService(TScmService tscmService, TCmsService tcmsService
			, TSrcService tsrcService, THrService thrService
			, TSysService tsysService, TNoteService tnoteService
			, TCimsService tcimsService, TWcmService  twcmService){
		
		this.tscmService  = tscmService;
	    this.tcmsService  = tcmsService;
	    this.tsrcService  = tsrcService;
	    this.thrService   = thrService;
	    this.tsysService  = tsysService;
	    this.tnoteService = tnoteService;
	    this.tcimsService = tcimsService;
	    this.twcmService  = twcmService;
	}
	
	private Object getService(String svcName) throws Exception {
		if ("tscm".equals(svcName)) {
			return this.tscmService;
		} else if ("tcms".equals(svcName)) {
			return this.tcmsService;
		} else if ("tsrc".equals(svcName)) {
			return this.tsrcService;
		} else if ("thr".equals(svcName)) {
			return this.thrService;
		} else if ("tsys".equals(svcName)) {
			return this.tsysService;
		} else if ("tnote".equals(svcName)) {
			return this.tnoteService;
		} else if ("tcims".equals(svcName)) {
			return this.tcimsService;
		} else if ("twcm".equals(svcName)) {
			return this.twcmService;
		} else if ("tfile".equals(svcName)) {
			return this.tsysService;
		} else {
			return null;
		}
	}
	
	@Transactional
	public void saveTblPoDataAll(List<Map<String,Object>> rsList) throws Exception {		
		tscmService.saveTblPoDataAll(rsList);
	}
	
	@Transactional
	public void saveTblCodesAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblCodesAll run");			
		
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
	}
	
	@Transactional
	public void saveTblBorgsAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblCustInfoAll run");			
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*
		if (msaIDs.contains("tscm")) {
			tscmService.saveTblBorgsAll(rsList);
		}		
		if (msaIDs.contains("tcms")) {
			tcmsService.saveTblBorgsAll(rsList);
		}		
		if (msaIDs.contains("tsrc")) {
			tsrcService.saveTblBorgsAll(rsList);
		}
		*/
	}
	
	@Transactional
	public void saveTblCustInfoAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblCustInfoAll run");		
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*
		if (msaIDs.contains("tscm")) {
			tscmService.saveTblCustInfoAll(rsList);
		}		
		if (msaIDs.contains("tcms")) {
			tcmsService.saveTblCustInfoAll(rsList);
		}		
		if (msaIDs.contains("tsrc")) {
			tsrcService.saveTblCustInfoAll(rsList);
		}
		*/
	}
	
	@Transactional
	public void saveTblVdInfoAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblCustVdInfoAll run");	
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*
		if (msaIDs.contains("tscm")) {
			tscmService.saveTblVdInfoAll(rsList);
		}		
		if (msaIDs.contains("tcms")) {
			tcmsService.saveTblVdInfoAll(rsList);
		}		
		if (msaIDs.contains("tsrc")) {
			tsrcService.saveTblVdInfoAll(rsList);
		}
		*/
	}
	
	@Transactional
	public void saveTblCustVdInfoAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblCustVdInfoAll run");			
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*
		if (msaIDs.contains("tscm")) {
			tscmService.saveTblCustVdInfoAll(rsList);
		}		
		if (msaIDs.contains("tcms")) {
			tcmsService.saveTblCustVdInfoAll(rsList);
		}		
		if (msaIDs.contains("tsrc")) {
			tsrcService.saveTblCustVdInfoAll(rsList);
		}
		*/
	}
	
	@Transactional
	public void saveTblVdPartnersAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblVdPartnersAll run");			
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*
		if (msaIDs.contains("tscm")) {
			tscmService.saveTblVdPartnersAll(rsList);
		}		
		if (msaIDs.contains("tcms")) {
			tcmsService.saveTblVdPartnersAll(rsList);
		}		
		if (msaIDs.contains("tsrc")) {
			tsrcService.saveTblVdPartnersAll(rsList);
		}
		*/
	}
	
	@Transactional
	public void saveTblUserInfoAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblUserInfoAll run");	
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*
		if (msaIDs.contains("tscm")) {
			tscmService.saveTblUserInfoAll(rsList);
		}		
		if (msaIDs.contains("tcms")) {
			tcmsService.saveTblUserInfoAll(rsList);
		}		
		if (msaIDs.contains("tsrc")) {
			tsrcService.saveTblUserInfoAll(rsList);
		}
		if (msaIDs.contains("tsys")) {
			tsysService.saveTblUserInfoAll(rsList);
		}
		*/
	}
	
	@Transactional
	public void saveTblUserCustAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblUserCustAll run");			
		
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*		
		if (msaIDs.contains("tscm")) {
			tscmService.saveTblUserCustAll(rsList);
		}		
		if (msaIDs.contains("tcms")) {
			tcmsService.saveTblUserCustAll(rsList);
		}		
		if (msaIDs.contains("tsrc")) {
			tsrcService.saveTblUserCustAll(rsList);
		}
		*/
	}
	
	@Transactional
	public void saveTblAttachAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblAttachAll run");			
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*
		if (msaIDs.contains("tnote")) {
			tnoteService.saveTblAttachAll(rsList);
		}
		*/
	}

	/**
	 * 사용자별 권한 데이터 수정 및 생성 메서드
	 * 
	 * @param  List<Map<String,Object>> rsList
	 * @throws Exception
	 */
	@Transactional
	public void saveTblUserPrivsAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblUserPrivsAll run");	
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
					
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
	}
	
	public List<Map<String,Object>> getTblSyncJobList(Map<String,Object> rsMap) throws Exception {		
		return tsysService.getTblSyncJobList(rsMap);
	}
	
	@Transactional
	public void saveTblCustItemsAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblCustItemsAll run ::: "+ functionNM);			
		
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*		
		if (msaIDs.contains("tscm")) {
			tscmService.saveTblCustItemsAll(rsList);
		}				
		if (msaIDs.contains("tsrc")) {
			tsrcService.saveTblCustItemsAll(rsList);
		}
		*/
	}
	
	@Transactional
	public void saveTblItemsAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblItemsAll run ::: "+ functionNM);			
		
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
	}
	
	@Transactional
	public void saveTblGpnItemsAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblGpnItemsAll run ::: "+ functionNM);			
		
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
	}
	
	@Transactional
	public void saveXmlCreate(List<String> msaIDs, String functionNM, Map<String,Object> commInfoMap) throws Exception {		
		logger.debug("saveRoleMapXml run ::: "+ functionNM);
		
		for (String svcNM : msaIDs) {
			logger.debug("saveRoleMapXml svcNM ::: "+ svcNM);
			Object svcObj = this.getService(svcNM);
			logger.debug("saveRoleMapXml svcObj.getClass() ::: "+ svcObj.getClass().getName());
			
			Class prmTypes[] = { Map.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { commInfoMap });			
		}
	}
	
	@Transactional
	public void saveTblUserPrivs4User(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTblUserPrivs4User run ::: "+ functionNM);			
		
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
		/*		
		if (msaIDs.contains("tsys")) {
			tsysService.saveTblUserPrivs4User(rsList);
		}				
		*/
	}
	
	@Transactional
	public void saveTwcmCodesAll(List<String> msaIDs, String functionNM, List<Map<String,Object>> rsList) throws Exception {		
		logger.debug("saveTwcmCodesAll run");		
		for (String svcNM : msaIDs) {
			Object svcObj = this.getService(svcNM);
			
			Class prmTypes[] = { List.class };
			Method method = svcObj.getClass().getDeclaredMethod(functionNM, prmTypes);
			method.invoke(svcObj, new Object[] { rsList });			
		}
	}

	/*	
	@Transactional
	public void saveTblCustItemsAll(List<Map<String,Object>> rsList) throws Exception {
		
		logger.debug("saveTblMstInfoAll run");
		for (Map<String,Object> rsInfo :rsList) {	
			rsInfo.put("SQLMODE", this.SQLMODE);
			logger.debug("saveTblMstInfoAll.rsInfo ::: "+ rsInfo.get("custID"));
			if (commMapper.existsTblCustItems(rsInfo) == 0) {
				commMapper.insTblCustItems(rsInfo);
			} else {
				commMapper.updTblCustItems(rsInfo);
			}
		}
	}
	*/

}