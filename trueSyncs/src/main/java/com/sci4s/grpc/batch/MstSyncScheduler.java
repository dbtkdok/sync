package com.sci4s.grpc.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.sci4s.grpc.svc.Channel;
import com.sci4s.grpc.svc.TSyncService;
import com.sci4s.grpc.svc.tsys.TSysService;
import com.sci4s.utils.AES256Util;
import com.sci4s.utils.DateUtil;

@RefreshScope
@Component
public class MstSyncScheduler {

	Logger logger = LoggerFactory.getLogger(MstSyncScheduler.class);
	
	@Value("${server.key}")
	String SVC_KEY;
	
	@Value("${json.inf.path}")
	String JSON_PATH;
	
	@Value("${mst.sync.running}")
	String SYNC_RUNNING;
	
	@Value("${rolemap.path}")
	String ROLEMAP_PATH;
	
	@Value("${menu.path}")
	String MENU_PATH;
	
	@Value("${file.path}")
	String FILE_PATH;
	
	boolean IS_SYNC_RUNNING = true;
	
	@Value("${msa.pids.uri}")
	String TSYS_URI;
	
	@Value("${msa.agentid}")
	String AGENT_ID;
	
	@Value("${msa.useruid}")
	String USER_UID;
	
	@Value("${msa.borguid}")
	String BORG_UID;
	
	@Value("${buffer.type}")
	String BUFFER_TYPE;
	
	@Value("${msa.pids.buffer}")
	String MSA_BUFFER_TYPE;
	
	@Value("${mst.thr.uri}")
	String MST_THR_URI;
	
	@Value("${mst.tcms.uri}")
	String MST_TCMS_URI;
	
	@Value("${mst.tcont.uri}")
	String MST_TCONT_URI;
	
	@Value("${mst.tcims.uri}")
	String MST_TCIMS_URI;
	
	@Value("${spring.application.name}")
	String APPLICATION_NAME;
	
	@Value("${msa.ip}")
	String MSA_IP;
	
	@Value("${mst.backup.cron}")
	String BAK_CRON;
	
	private String CS_KEY;
	
	private Map<String,List<String>> targetMap;
	private Map<String,String> funtionMap;
	private long fiexdDelay;
	private long initDelay;
	private long histfiexdDelay;
	private long histinitDelay;
	
	private ThreadPoolTaskScheduler scheduler;
	
	Map<String, Object> commInfoMap;
	
	private TSyncService tsyncService;
	private TSysService  tsysService;
	private Channel channel;
//	
	@Autowired
//	public MstSyncScheduler(Channel channel) {	
	public MstSyncScheduler(TSyncService tsyncService, TSysService tsysService, Channel channel) {
		this.channel = channel;
		this.tsyncService = tsyncService;
		this.tsysService  = tsysService;
	}
	
	/**
	 * 
	 * 
	 */
	@PostConstruct
    private void init() {
		try { 
    		int julianDate  = DateUtil.toJulian(new Date());				
    		AES256Util aes256 = new AES256Util();				
			// csKey=julianDate|agentID|userUID|borgUID|userIP|serverIP
	    	String csKey  = julianDate+"|"+ this.AGENT_ID +"|"+ this.APPLICATION_NAME +"_SyncScheduler|"+ this.BORG_UID +"|"+ MSA_IP +"|"+ MSA_IP;
	    	this.CS_KEY = aes256.encrypt(csKey);	    	
	    	this.IS_SYNC_RUNNING = Boolean.parseBoolean(this.SYNC_RUNNING);

	    	commInfoMap = new HashMap<String, Object>();
	    	commInfoMap.put("agentID", this.AGENT_ID);
	    	commInfoMap.put("userUID", this.USER_UID);	    	
	    	commInfoMap.put("tsys",    this.TSYS_URI);
	    	commInfoMap.put("thr",     this.MST_THR_URI);
	    	commInfoMap.put("tcms",    this.MST_TCMS_URI);
	    	commInfoMap.put("tcont",   this.MST_TCONT_URI);
	    	commInfoMap.put("tcims",   this.MST_TCIMS_URI);
	    	commInfoMap.put("CS_KEY",   this.CS_KEY);
	    	commInfoMap.put("USER_UID", this.USER_UID);
	    	commInfoMap.put("BORG_UID", this.BORG_UID);
	    	commInfoMap.put("MSA_IP", this.MSA_IP);
	    	commInfoMap.put("BUFFER_TYPE", this.BUFFER_TYPE);
	    	commInfoMap.put("JSON_PATH", this.JSON_PATH);
	    	commInfoMap.put("SYNC_RUNNING", this.SYNC_RUNNING);
	    	
	    	
	    	//logger.info("MST_CODES_TARGETS::::::::::"+ this.MST_CODES_TARGETS +"|||"+ this.MST_CODES_TARGETS.indexOf("|"));   
	    	logger.info("mst.backup.cron::::::::::"+ this.BAK_CRON);
	    	
	    	Map<String,Object> params = new HashMap<String,Object>();
	    	params.put("agentID", this.AGENT_ID);
	    	params.put("userUID", this.USER_UID);	    
//	    	params.put("msaID", "tsys");	    
	    	params.put("msaID", this.TSYS_URI);	
	    	
	    	String jsonRet = this.channel.getSyncConfig(params, this.BUFFER_TYPE);
	    	Map<String, Object> param = new ObjectMapper().readValue(jsonRet, Map.class);
	    	List<Map<String,Object>> val = (List<Map<String, Object>>) param.get("results");
	    	targetMap = new HashMap<String,List<String>>();	
	    	funtionMap = new HashMap<String,String>();
	    	fiexdDelay = 0;
			initDelay = 0;
	    	
	    	if(val != null && val.size() > 0) {
	    		scheduler = new ThreadPoolTaskScheduler();
	    		scheduler.initialize();
	    		for(int ii=0; ii<val.size(); ii++) {
		    		Map<String, Object> paramMap = val.get(ii);
		    		List<String> targets = new ArrayList<String>();
		    		String target = paramMap.get("targets").toString();
		    		String pID = paramMap.get("pID").toString();
		    		String ifMsa = paramMap.get("ifMSA").toString();
		    		String functionNM = paramMap.get("functionNM").toString();
		    		String type = paramMap.get("type").toString();
		    		String mapperPID = (paramMap.get("mapperPID")==null?"":paramMap.get("mapperPID").toString());
		    		String methodNM = (paramMap.get("methodNM")==null?"":paramMap.get("methodNM").toString());
		    		
		    		long fixedDelay = Integer.parseInt("" + paramMap.get("fixedDelay"));
		    		long initialDelay = Integer.parseInt("" + paramMap.get("initialDelay"));
		    		
		    		PeriodicTrigger periodicTrigger = new PeriodicTrigger(fixedDelay, TimeUnit.SECONDS);
		    		periodicTrigger.setInitialDelay(initialDelay);
		    		periodicTrigger.setFixedRate(true);
		    		
		    		if (target.indexOf("|") >= 0) {
						String[] msaIDs = target.split("\\|");
						for (String msaID : msaIDs) {
							targets.add(msaID);
						}
					} else {
						targets.add(target);
					}
		    		
		    		targetMap.put(pID, targets);
		    		funtionMap.put(pID, functionNM);
		    		
		    		Map<String, Object> rsMap = new HashMap<String, Object>();
		        	rsMap.putAll(commInfoMap);
		    		
		    		if("SYNC0017".equals(pID)) {
		    			rsMap.put("pathNM", this.ROLEMAP_PATH);
		    		} else if("SYNC0013".equals(pID)) {
		    			rsMap.put("pathNM", this.MENU_PATH);
		    		} else if("SYNC0016".equals(pID)) {
		    			rsMap.put("pathNM", this.FILE_PATH);
		    		} else if("sync".equals(type)) {
		    			fiexdDelay = fixedDelay;
		    			initDelay = initialDelay;
		    		} else if("hist".equals(type)) {
		    			histfiexdDelay = fixedDelay;
		    			histinitDelay = initialDelay;
		    		}
		    		
		    		if("syncCommon4XmlCreate".equals(type)) {
		    			
		    		} else {
		    			functionNM = methodNM;
		    		}
		    		
		    		if(!"sync".equals(type) && !"hist".equals(type)) { // DB 싱크 처리 시 스케줄러는 맨 마지막에 넣어줌
		    			scheduler.schedule(new RunnableTask(pID
				    			, mapperPID
				    			, ifMsa
				    			, targets
				    			, functionNM
				    			, rsMap
				    			, type
				    			, null
				    			, null
				    			, this.tsyncService
				    			, this.tsysService
				    			, this.channel), periodicTrigger);
		    		}
		    	}
	    		
	    		scheduler = new ThreadPoolTaskScheduler();
	    		scheduler.initialize();
	    		
	    		PeriodicTrigger periodicTrigger = new PeriodicTrigger(fiexdDelay, TimeUnit.SECONDS);
	    		periodicTrigger.setInitialDelay(initDelay);
	    		periodicTrigger.setFixedRate(true);
	    		// DB 싱크 처리 시 스케줄러
	    		scheduler.schedule(new RunnableTask(""
		    			, ""
		    			, ""
		    			, null
		    			, ""
		    			, commInfoMap
		    			, "sync"
		    			, targetMap
		    			, funtionMap
		    			, this.tsyncService
		    			, this.tsysService
		    			, this.channel), periodicTrigger);
	    		
	    		scheduler = new ThreadPoolTaskScheduler();
	    		scheduler.initialize();
	    		
	    		PeriodicTrigger periodicTrigger2 = new PeriodicTrigger(histfiexdDelay, TimeUnit.SECONDS);
	    		periodicTrigger2.setInitialDelay(histinitDelay);
	    		periodicTrigger.setFixedRate(true);
	    		// DB 싱크 처리 시 스케줄러
	    		scheduler.schedule(new RunnableTask(""
		    			, ""
		    			, ""
		    			, null
		    			, ""
		    			, commInfoMap
		    			, "hist"
		    			, targetMap
		    			, funtionMap
		    			, this.tsyncService
		    			, this.tsysService
		    			, this.channel), periodicTrigger2);
	    		
	    	}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
    }
//	
//	private String getPrintStackTrace(Exception e) {        
//        StringWriter errors = new StringWriter();
//        e.printStackTrace(new PrintWriter(errors));         
//        return errors.toString();         
//    }
//	
//	/**
//     * 전체 데이터 싱크 처리용 스케줄러
//     *  
//     */ 
//    @Scheduled(fixedDelayString="${mst.syncJob.fixedDelay}", initialDelayString="${mst.syncJob.initialDelay}")
//    public void procTblSyncJobHist() {
//    	long start = System.currentTimeMillis();
//    	long end   = 0;
//    	logger.info("==========Remote "+ this.TSYS_URI +" SyncJobHist Call Begin============="+ new Date());    	
//    	List<Map<String,Object>> retList = null;
//    	Map<String,Object> rsMap = null;
//    	try {
//    		if (this.IS_SYNC_RUNNING) {
//    			rsMap = new HashMap<String,Object>();
//    			rsMap.put("agentID",     this.AGENT_ID);
//    			rsMap.put("userUID",     this.USER_UID);  
//		
//    			retList = tsysService.getTblSyncJobHist(rsMap);    	
//    			if (retList != null && retList.size() > 0) {
//    				for (Map<String,Object> retMap : retList) {    					
//    					String ifUID      = ""+ retMap.get("ifUID");
//    					String pID        = ""+ retMap.get("pID");
//    					String results    = ""+ retMap.get("results");
//    					String jsonFileNM = ""+ retMap.get("jsonFileNM");
//    					
//    					rsMap.put("ifUID", ifUID);
//    					rsMap.put("procStartDT", "Y");
//    					try { tsysService.updTblSyncJobHist(rsMap); } catch(Exception e0) { } //실행시작시간
//    					try {
//    						String fileFullPath = this.JSON_PATH + jsonFileNM;
//   			        		JSONObject jsonObj = (JSONObject) new JSONParser().parse(new FileReader(fileFullPath));	
//   			        		List<Map<String,Object>> rsList = new ObjectMapper().readValue(jsonObj.get("results").toString(), new TypeReference<List<Map<String,Object>>>(){});
//   			        		if (rsList != null) {
//   			        			String funcName = funtionMap.get(pID);   
//   			        			List<String> target = targetMap.get(pID);
//   			        			
//		    					if (pID.equals("SCM4130")) { // 일반계약발주
//		    						tsyncService.saveTblPoDataAll(rsList);
//		    					} else if (pID.equals("SYNC0001")) { // 코드정보
//		    						tsyncService.saveTblCodesAll(target, funcName, rsList);
//		    					} else if (pID.equals("SYNC0002")) { // 첨부파일
//		    						tsyncService.saveTblAttachAll(target, funcName, rsList); 
//		    					} else if (pID.equals("SYNC0003")) { // 부서정보
//		    						tsyncService.saveTblBorgsAll(target, funcName, rsList); 
//		    					} else if (pID.equals("SYNC0004")) { // 고객사정보
//		    						tsyncService.saveTblCustInfoAll(target, funcName, rsList); 
//		    					} else if (pID.equals("SYNC0005")) { // 협력사정보
//		    						tsyncService.saveTblVdInfoAll(target, funcName, rsList); 
//		    					} else if (pID.equals("SYNC0006")) { // 고객 협력사 정보
//		    						tsyncService.saveTblCustVdInfoAll(target, funcName, rsList); 
//		    					} else if (pID.equals("SYNC0007")) { // 협력사 거래고객사 정보
//		    						tsyncService.saveTblVdPartnersAll(target, funcName, rsList);
//		    					} else if (pID.equals("SYNC0008")) { // 사용자정보
//		    						tsyncService.saveTblUserInfoAll(target, funcName, rsList); 
//		    					} else if (pID.equals("SYNC0009")) { // 운영사 담당자 고객사
//		    						tsyncService.saveTblUserCustAll(target, funcName, rsList);
//		    					} else if (pID.equals("SYNC0010")) { // 사용자 권한 정보
//		    						tsyncService.saveTblUserPrivsAll(target, funcName, rsList);
//		    					} else if (pID.equals("SYNC0011")) { // 고객사 품목정보
//		    						tsyncService.saveTblCustItemsAll(target, funcName, rsList);
//		    					} else if (pID.equals("SYNC0012")) { // 사용자권한 변경정보
//		    						tsyncService.saveTblUserPrivs4User(target, funcName, rsList);
//		    					} else if (pID.equals("SYNC0014")) { // 품목정보
//		    						tsyncService.saveTblItemsAll(target, funcName, rsList);
//		    					} else if (pID.equals("SYNC0015")) { // 품목정보
//		    						tsyncService.saveTblGpnItemsAll(target, funcName, rsList);
//		    					}
//   			        		}
//	    					rsMap.put("ifPSTS", "Y");
//    						rsMap.put("results", results +"\n"+ "2) "+ retMap.get("pNM") +" 완료");
//    					} catch(Exception ex) {
//    						rsMap.put("ifPSTS", "E");
//    						String errMsg = this.getPrintStackTrace(ex);
//    						errMsg = errMsg.split("\\Q; (conn=\\E")[0].replaceAll(System.getProperty("line.separator"), "");
//    						rsMap.put("results", results +"\n"+ "2) "+ errMsg);
//    			        }
//    					rsMap.put("procEndDT", "Y");
//    					try { tsysService.updTblSyncJobHist(rsMap); } catch(Exception e0) { }
//    				}
//    			}
//    		} else {
//    			logger.info("IS_SYNC_RUNNING ::: "+ this.SYNC_RUNNING);
//    		}
//    		end = System.currentTimeMillis();
//    	} catch(Exception ex) {
//    		ex.printStackTrace();
//        } finally {
//         	if (retList != null) { retList = null; }
//         	if (rsMap != null) { rsMap = null; }
//        }
//    	logger.info("==========Remote "+ this.TSYS_URI +" SyncJobHist Call Finish============="+ ( end - start ) +"ms");
//    }
	
//	
//	/**
//     * 마이크로 서비스 IF 대상 데이터를 조회하는 클라이언트 서비스
//     * 발주인터페이스 : PID|agentID|userUID=SCM4130|13|1
//     */
////    @Scheduled(fixedDelayString="${mst.podata.fixedDelay}", initialDelayString="${mst.podata.initialDelay}")
//    private void syncPoData4NoneIF() {      
//    	this.syncCommon4NoneIF("SCM4130", "SCM4130", this.targetMap.get("SCM4130").get(0), "getIFData4NoneIF");
//    }
//    
//	/**
//     * 코드 인터페이스
//     */
////    @Scheduled(fixedDelayString="${mst.codes.fixedDelay}", initialDelayString="${mst.codes.initialDelay}")
//    private void syncCodeData4NoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0001", "TSY0100", this.targetMap.get("SYNC0001").get(0), "syncMstInfoTsys");
//    }
//    
//    /**
//     * 부서마스터 조회용 스케줄러
//     */
////    @Scheduled(fixedDelayString="${mst.borgs.fixedDelay}", initialDelayString="${mst.borgs.initialDelay}")
//    private void syncBorgData4NoneIF() {       
//    	this.syncCommon4NoneIF("SYNC0003", "TSYNC001", this.targetMap.get("SYNC0003").get(0), "syncMstInfoTsys");
//    }
//    
//	/**
//     * 고객사마스터 조회용 스케줄러
//     */
////    @Scheduled(fixedDelayString="${mst.custinfo.fixedDelay}", initialDelayString="${mst.custinfo.initialDelay}")
//    private void syncCustInfo4NoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0004", "TSYNC001", this.targetMap.get("SYNC0004").get(0), "syncMstInfoTsys");
//    }
//    
//    /**
//     * 협력사 마스터 조회용 스케줄러
//     */
////    @Scheduled(fixedDelayString="${mst.vdinfo.fixedDelay}", initialDelayString="${mst.vdinfo.initialDelay}")
//    private void syncVdInfo4NoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0005", "TSYNC001", this.targetMap.get("SYNC0005").get(0), "syncMstInfoTsys");
//    }
//    
//    /**
//     * 고객협력사 마스터 조회용 스케줄러
//     */
////    @Scheduled(fixedDelayString="${mst.custvdinfo.fixedDelay}", initialDelayString="${mst.custvdinfo.initialDelay}")
//    private void syncCustVdInfo4NoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0006", "TSYNC001", this.targetMap.get("SYNC0006").get(0), "syncMstInfoTsys");
//    }
//    
//    /**
//     * 협력사 거래처 마스터 조회용 스케줄러
//     */
////    @Scheduled(fixedDelayString="${mst.vdpartners.fixedDelay}", initialDelayString="${mst.vdpartners.initialDelay}")
//    private void syncVdPartners4NoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0007", "TSYNC001", this.targetMap.get("SYNC0007").get(0), "syncMstInfoTsys");
//    }
//    
//    /**
//     * 사용자 마스터 조회용 스케줄러
//     */
////    @Scheduled(fixedDelayString="${mst.userinfo.fixedDelay}", initialDelayString="${mst.userinfo.initialDelay}")
//    private void syncUserInfo4NoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0008", "TSYNC001", this.targetMap.get("SYNC0008").get(0), "syncMstInfoTsys");
//    }
//
//    /**
//     * 운영담당자 고객사 정보 조회용 스케줄러
//     */
////    @Scheduled(fixedDelayString="${mst.usercust.fixedDelay}", initialDelayString="${mst.usercust.initialDelay}")
//    private void syncUserCust4NoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0009", "TSYNC001", this.targetMap.get("SYNC0009").get(0), "syncMstInfoTsys");
//    }
//    
//    
//    /**
//     * 첨부파일  조회용 스케줄러
//     *  
//     */
////    @Scheduled(fixedDelayString="${mst.attach.fixedDelay}", initialDelayString="${mst.attach.initialDelay}")
//    public void syncAttatch4NoneIF() {
//    	this.syncCommon4NoneIF("SYNC0002", "TSY0100", this.targetMap.get("SYNC0002").get(0), "syncMstInfoTsys");
//    }
//    /**
//     * roleMap xml 생성 스케줄러
//     * @return 
//     *  
//     */
////    @Scheduled(fixedDelayString="${mst.rolemap.fixedDelay}", initialDelayString="${mst.rolemap.initialDelay}")
//    public void syncRoleMapXml() {
//    	Map<String, Object> rsMap = new HashMap<String, Object>();
//    	rsMap.putAll(commInfoMap);
////    	rsMap.put("pathNM", this.ROLEMAP_PATH);
//    	rsMap.put("pathNM", "D:/tmp/upload/rolemap");
//    	
//    	System.out.println(" this.targetMap.get(\"SYNC0017\") :::: " + this.targetMap.get("SYNC0017"));
//    	
////    	return this.syncCommon4XmlCreate("SYNC0017", "tsys", this.targetMap.get("SYNC0017"), "saveRoleMapXml", rsMap);
//    }
//    
//    /**
//     * 권한별 메뉴 xml 인터페이스  싱크용 스케줄러
//     * saveMenuXml
//     */
////    @Scheduled(fixedDelayString="${mst.menuxml.fixedDelay}", initialDelayString="${mst.menuxml.initialDelay}")
//    private void syncRoleMenuNoneIF() {  
//    	Map<String, Object> rsMap = new HashMap<String, Object>();
//    	rsMap.putAll(commInfoMap);
//    	rsMap.put("pathNM", this.MENU_PATH);
//    	
//    	this.syncCommon4XmlCreate("SYNC0013", "tsys", this.targetMap.get("SYNC0013"), "saveMenuXml", rsMap);
//    }
//    
////    @Scheduled(fixedDelayString="${mst.dictionarys.fixedDelay}", initialDelayString="${mst.dictionarys.initialDelay}")
//    private void syncDictionaryNoneIF() {   	
//    	Map<String, Object> rsMap = new HashMap<String, Object>();
//    	rsMap.putAll(commInfoMap);
//    	rsMap.put("pathNM", this.FILE_PATH);
//    	
//    	this.syncCommon4XmlCreate("SYNC0016", "tsys", this.targetMap.get("SYNC0016"), "saveDictionaryXml", rsMap);
//    }
//    
//    
//    public void syncCommon4XmlCreate(String pid, String msaID, List<String> msaIDs, String functionNM, Map<String,Object> rsList) {
//    	long start = System.currentTimeMillis();
//    	long end = 0;
//    	
//    	logger.info("==========Remote "+ commInfoMap.get(msaID) +" "+ pid +" Call Begin============="+ new Date());
//    	logger.info("==========Remote "+ msaIDs);
//    	
//    	try {
//			tsyncService.saveXmlCreate(msaIDs, functionNM, rsList);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	end = System.currentTimeMillis();
//
//    	logger.info("==========Remote "+ commInfoMap.get(msaID) +" "+ pid +" Call Finish============="+ ( end - start ) +"ms");
////		return null;
//    	
////    	return Runnable();
//    		
////		};
//    }
//    
//    public void syncCommon4NoneIF(String pid, String ifPID, String msaID, String methodNM) {
//    	long start = System.currentTimeMillis();
//    	long end   = 0;
//    	logger.info("==========Remote "+ commInfoMap.get(msaID) +" "+ pid +" Call Begin============="+ new Date());    	
//    	String svcKey = com.sci4s.util.Config.SVC_KEY.toUpperCase();
//    	List<Map<String,Object>> rsList = null;
//    	Map<String,Object> rsMap = null;
//    	try {
//    		if (this.IS_SYNC_RUNNING) {
//    		//if (this.IS_SYNC_RUNNING && pid.equals("SYNC0012")) {
//    			// select pID, pNM, tableNM, ifMSA, ifParamIDs, targets, functionNM 
//    			//   from tbl_syncjob 
//    			//  where agentID = #{agentID} and pID = #{pID}
//    			rsMap = new HashMap<String,Object>();
//    			rsMap.putAll(commInfoMap);
//    			rsMap.put("pID",    pid);
//    			rsMap.put("ifPID",  ifPID);
//    			rsMap.put("ifRSTS", "N");
//    			rsMap.put("ifMSA",  ""+ commInfoMap.get(msaID));
//    			
//    			rsList = tsyncService.getTblSyncJobList(rsMap);
//    			if (rsList != null && rsList.size() > 0) {
//    				String ifParamIDs  = ""+ rsList.get(0).get("ifParamIDs");
//    				String ifParamVals = ifParamIDs;
//    				String[] ids = null;
//    				if (ifParamIDs.indexOf("|") >= 0) {
//    					ids = ifParamIDs.split("\\|");
//    				} else {
//    					ids = new String[] { ifParamIDs };
//    				}
//    				for (int ii=0; ii<ids.length; ii++) {
//    					logger.info("ids :::::::::::: "+ ids[ii]);
//    					
//    					if ("tblName".equals(ids[ii])) {
//    						ifParamVals = ifParamVals.replace(ids[ii], ""+ rsList.get(0).get("tableNM"));
//    					} else if ("svcKey".equals(ids[ii])) {
//    						ifParamVals = ifParamVals.replace(ids[ii], svcKey);
//    					} else if ("PID".equals(ids[ii])) {
//    						ifParamVals = ifParamVals.replace(ids[ii], ifPID);
//    					} else {
//    						ifParamVals = ifParamVals.replace(ids[ii], ""+ commInfoMap.get(ids[ii]));
//    					}
//    				}
//    				logger.info("ifParamVals :::::::::::: "+ ifParamVals);
//    				
//    				rsMap.put("pNM",        rsList.get(0).get("pNM"));
//    				rsMap.put("tableNM",    rsList.get(0).get("tableNM"));
//    				rsMap.put("svcKey",     svcKey);
//    				rsMap.put("ifParamIDs", ifParamIDs);
//    				rsMap.put("ifParamVals",ifParamVals);    				
//    			}
//    			logger.info("syncCommon4NoneIF :::::::::::: "+ rsMap);
//    			
//    			tsysService.insTblSyncJobHist(rsMap);    			
//    			long ifUID = 0;
//    			if (rsMap.containsKey("ifUID")) {
//    				ifUID = Long.parseLong(rsMap.get("ifUID").toString());
//    			}
//    			logger.info("ifUID============="+ ifUID); 
//    			
//    			rsMap.put("ifUID", ifUID);
//
//    			String jsonFileNM = null;
//    			if ("getIFData4NoneIF".equals(methodNM)) {
//    				jsonFileNM = this.getIFData4NoneIF(rsMap);
//    			} else {
//    				jsonFileNM = this.syncMstInfoTsys(rsMap);
//    			}
//	    		if (jsonFileNM != null) { // 데이터를 저장함
//	    			rsMap.put("jsonFileNM", jsonFileNM);
//	    			rsMap.put("ifRSTS", "Y");
//	    			rsMap.put("ifPSTS", "T");
//	    			rsMap.put("results", "1) a json file is create successfully.");
//	    		} else {
//	    			rsMap.put("ifRSTS", "Y");
//	    			rsMap.put("results", "1) no data");
//	    		}
//	    		tsysService.updTblSyncJobHist(rsMap);
//    		} else {
//    			logger.info("IS_SYNC_RUNNING ::: "+ this.SYNC_RUNNING);
//    		}
//    		end = System.currentTimeMillis();
//    	} catch(Exception ex) {
//    		if (this.IS_SYNC_RUNNING) { 
//    			rsMap.put("ifRSTS", "E");
//    			rsMap.put("results", this.getPrintStackTrace(ex));
//    			try { tsysService.updTblSyncJobHist(rsMap); } catch(Exception e0) { }
//    		}
//        } finally {
//         	if (rsList != null) { rsList = null; }
//         	if (rsMap  != null) { rsMap  = null; }
//        }
//    	logger.info("==========Remote "+ commInfoMap.get(msaID) +" "+ pid +" Call Finish============="+ ( end - start ) +"ms");
//    }
//
//    
//    /**
//     * 권한정보 인터페이스  싱크용 스케줄러
//     * 
//     */
//    @Scheduled(fixedDelayString="${mst.userprivs.fixedDelay}", initialDelayString="${mst.userprivs.initialDelay}")
//    private void syncUserPrivsNoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0010", "TSY0100", "tsys", "syncMstInfoTsys");
//    	/*    	
//    	long start = System.currentTimeMillis();
//    	long end   = 0;
//    	logger.info("==========Remote "+ this.TSYS_URI +" UserPrivs Call Begin============="+ new Date()); 
//    	String svcKey = com.sci4s.util.Config.SVC_KEY.toUpperCase();
//    	List<Map<String,Object>> rsList = null;
//    	Map<String,Object> rsMap = null;
//    	try {
//    		if (this.IS_SYNC_RUNNING) {    			
//    			rsMap = new HashMap<String,Object>();
//    			rsMap.put("ifPID",       "TSY0100");
//    			rsMap.put("pID",         "SYNC0010");
//    			rsMap.put("pNM",         "사용자권한 연계");
//    			rsMap.put("tableNM",     "tbl_rolescopesif");
//    			rsMap.put("svcKey",      svcKey);
//    			rsMap.put("ifMSA",       this.TSYS_URI);
//    			rsMap.put("ifParamIDs",  "agentID|tblName|svcKey");
//    			rsMap.put("ifParamVals", this.AGENT_ID +"|tbl_rolescopesif|"+ svcKey);
//    			rsMap.put("agentID",     this.AGENT_ID);
//    			rsMap.put("userUID",     this.USER_UID);   
//    			rsMap.put("ifRSTS",      "N");
//    			
//    			tsysService.insTblSyncJobHist(rsMap);    			
//    			long ifUID = 0;
//    			if (rsMap.containsKey("ifUID")) {
//    				ifUID = Long.parseLong(rsMap.get("ifUID").toString());
//    			}
//    			logger.info("ifUID============="+ ifUID); 
//    			
//    			rsMap.put("ifUID", ifUID);
//    			String jsonFileNM = this.syncMstInfoTsys(rsMap);
//	    		if (jsonFileNM != null) { // 데이터를 저장함
//	    			rsMap.put("jsonFileNM", jsonFileNM);
//	    			rsMap.put("ifRSTS", "Y");
//	    			rsMap.put("ifPSTS", "T");
//	    			rsMap.put("results", "1) a json file is create successfully.");
//	    		} else {
//	    			rsMap.put("ifRSTS", "Y");
//	    			rsMap.put("results", "1) no data");
//	    		}  
//	    		tsysService.updTblSyncJobHist(rsMap);
//    		} else {
//    			logger.info("syncUserPrivsNoneIF.IS_SYNC_RUNNING ::: "+ this.SYNC_RUNNING);
//    		}
//    		end = System.currentTimeMillis();
//    	} catch(Exception ex) {
//    		if (this.IS_SYNC_RUNNING) { 
//    			rsMap.put("ifRSTS", "E");
//    			rsMap.put("results", this.getPrintStackTrace(ex));
//    			try { tsysService.updTblSyncJobHist(rsMap); } catch(Exception e0) { }
//    		}
//        } finally {
//         	if (rsList != null) { rsList = null; }
//         	if (rsMap != null) { rsMap = null; }
//        }
//    	logger.info("==========Remote "+ this.TSYS_URI +" UserPrivs Call Finish============="+ ( end - start ) +"ms");
//    	*/
//    }
//    
//    /**
//     * 고객사 품목정보 인터페이스  싱크용 스케줄러
//     * saveTblCustItemsAll
//     */
////    @Scheduled(fixedDelayString="${mst.custitems.fixedDelay}", initialDelayString="${mst.custitems.initialDelay}")
//    private void syncCustItemsNoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0011", "TSY0100", "tcms", "syncMstInfoTsys");
//    }
//    
//    
//    /**
//     * 사용자 권한정보 변경 인터페이스  싱크용 스케줄러
//     * saveTblUserPrivs4User
//     */
////    @Scheduled(fixedDelayString="${mst.custitems.fixedDelay}", initialDelayString="${mst.custitems.initialDelay}")
//    private void syncUserPrivs4UserNoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0012", "TSY0100", "thr", "syncMstInfoTsys");
//    }
//    
//    /**
//     * 품목정보 인터페이스  싱크용 스케줄러
//     * saveTblItemsAll
//     */
////    @Scheduled(fixedDelayString="${mst.items.fixedDelay}", initialDelayString="${mst.items.initialDelay}")
//    private void syncItemsNoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0014", "TSY0100", "tcms", "syncMstInfoTsys");
//    }
//    
//    /**
//     * 매핑 품목정보 인터페이스  싱크용 스케줄러
//     * saveTblGpnItemsAll
//     */
////    @Scheduled(fixedDelayString="${mst.gpnItems.fixedDelay}", initialDelayString="${mst.gpnItems.initialDelay}")
//    private void syncGpnItemsNoneIF() {        	
//    	this.syncCommon4NoneIF("SYNC0015", "TSY0100", "tcms", "syncMstInfoTsys");
//    }
//    
//    /**
//     * 연계이력 테이블 백업용 스케줄러
//     *  
//     */
//    @Scheduled(cron="${mst.backup.cron}")
//    public void bakTblSyncJobHist() {
//    	long start = System.currentTimeMillis();
//    	long end   = 0;
//    	logger.info("==========Remote "+ this.TSYS_URI +" backup TblSyncJobHist Call Call Begin============="+ new Date());    	
//    	Map<String,Object> rsMap = null;
//    	try {
//    		if (this.IS_SYNC_RUNNING) {
//    			rsMap = new HashMap<String,Object>();
//    			rsMap.put("agentID", this.AGENT_ID);
//    			rsMap.put("userUID", this.USER_UID);   
//
//    			tsysService.bakTblSyncJobHist(rsMap);
//    		} else {
//    			logger.info("IS_SYNC_RUNNING ::: "+ this.SYNC_RUNNING);
//    		}
//    		end = System.currentTimeMillis();
//    	} catch(Exception ex) {
//    		ex.printStackTrace();
//        } finally {
//         	if (rsMap != null) { rsMap = null; }
//        }
//    	logger.info("==========Remote "+ this.TSYS_URI +" backup TblSyncJobHist Call Finish============="+ ( end - start ) +"ms");
//    }
//    
//    /**
//     * 품목마스터 조회용 스케줄러
//     *      
//    //@Scheduled(fixedDelayString="${mst.custitems.fixedDelay}")
//    public void runner4CustItems() {
//    	long start = System.currentTimeMillis();
//    	long end   = 0;
//    	logger.info("==========Remote "+ this.MST_TCMS_URI +" CustItems Call Begin============="+ new Date()); 
//    	String svcKey = com.sci4s.util.Config.SVC_KEY.toUpperCase();
//    	List<Map<String,Object>> rsList = null;
//    	try {
//    		if (this.IS_SYNC_RUNNING) {
//	    		String jsonRet = syncMstInfo(this.MST_TCMS_URI, "tbl_custitems", this.APPLICATION_NAME, svcKey);
//	    		if (jsonRet != null) { // 데이터를 저장함
//	        		JSONObject jsonObj = (JSONObject) new JSONParser().parse(jsonRet);	
//	        		//System.out.println("jsonObj.get(\"results\").toString() ::: "+ jsonObj.get("results"));
//	        		rsList = new ObjectMapper().readValue(jsonObj.get("results").toString(), new TypeReference<List<Map<String,Object>>>(){});
//	        		if (rsList != null) { 
//	        			
//	        			System.out.println("..............데이터 저장 시작");
//	        			tsyncService.saveTblCustItemsAll(rsList); 
//	        			System.out.println("..............데이터 저장 끝");
//	        		}
//		    	}
//    		} else {
//    			logger.info("IS_SYNC_RUNNING ::: "+ this.SYNC_RUNNING);
//    		}
//    		end = System.currentTimeMillis();
//    	} catch(Exception ex) {
//         	ex.printStackTrace();
//        } finally {
//         	if (rsList != null) { rsList = null; }
//        }
//    	logger.info("==========Remote "+ this.MST_TCMS_URI +" CustItems Call Finish============="+ ( end - start ) +"ms");
//    }
//    */
//    
//    /**
//     * 모든 마스터 Data를 관리하는 서비스에 Native로 구현한 getMstInfo를 호출하여 싱크할 마스터 정보를 조회하는데 사용함.
//     * 싱크할 마스터 Data를 관리하는 서비스는  tbl_infinfo와 tbl_infinfohist 테이블를 생성해야 하며,
//     * 싱크할 데이블에 trg_ins_custInfo, trg_upd_custInfo 트리거를 구혀해야 함.
//     *  
//     * @param String MST_URL
//     * @param String tblNM
//     * @param String appNM
//     * @param String svcKey
//     * @return String results
//
//    private String syncMstInfo(String MST_URL, String tblNM, String appNM, String svcKey) {    	
//    	try {
//        	MsaApiGrpc.MsaApiBlockingStub stub = MsaApiGrpc.newBlockingStub(this.channel.openChannel(MST_URL));        	
//        	SciRIO.ReqMsg reqMsg = SciRIO.ReqMsg.newBuilder()
//        			.setAgentID(this.AGENT_ID)
//    				.setMsg(tblNM +"|"+ appNM +"@"+ svcKey)
//    				.build();
//        	
//        	RetMsg retMsg = stub.getMstInfo(reqMsg);
//        	String jsonRet = null;
//    		if (retMsg.getErrCode().equals("0")) {
//    			if (retMsg.getResults().indexOf("NO_DATA") >= 0) {
//    				jsonRet = null;
//    			} else {
//    				jsonRet = retMsg.getResults();
//    			}
//    		}
//    		this.channel.closeChannel();
//        	return jsonRet;
//        } catch(Exception ex) {
//        	ex.printStackTrace();
//        	return null;
//        }
//    }
//    */
//    /**
//     * tsys 서비스에 commonService로 구현한 getMstInfo를 호출하여 싱크할 마스터 정보를 조회하는데 사용함.
//     * 싱크할 마스터 Data를 관리하는 서비스는  tbl_infinfo와 tbl_infinfohist 테이블를 생성해야 하며,
//     * 싱크할 데이블에 trg_ins_custInfo, trg_upd_custInfo 트리거를 구혀해야 함.
//     *  
//     * @param Map<String,Object> rsMap
//     * @return String results
//     */
//    private String syncMstInfoTsys(Map<String,Object> rsMap) {    	
//    	try {
//    		String tblNM = rsMap.get("tableNM").toString();
//        	String jsonRet = null;
//        	String type = "";
//        	rsMap.put("CS_KEY", this.CS_KEY);
//        	rsMap.put("USER_UID", this.USER_UID);
//        	rsMap.put("BORG_UID", this.BORG_UID);
//        	rsMap.put("AGENT_ID", this.AGENT_ID);
//        	rsMap.put("MSA_IP", this.MSA_IP);
//        	rsMap.put("GRPC_URI", rsMap.get("ifMSA").toString());
//        	
//        	if ("tbl_codes".equals(tblNM) || "tbl_attach".equals(tblNM) || "tbl_rolescopesif".equals(tblNM)
//        			 || "tbl_rolemenuif".equals(tblNM)	|| "tbl_dictionaryif".equals(tblNM)
//	        		) {
//	   			type = "DATA";
//	   		}
//        	
//        	jsonRet = this.channel.syncMstInfoTsys(rsMap, this.BUFFER_TYPE, type);
//        	
//        	if (jsonRet != null && jsonRet != "") {
//        		jsonRet = this.saveJsonFile(rsMap.get("pID").toString(), jsonRet);
//   			}
//        	
//    		this.channel.closeChannel();
//        	return jsonRet;
//        } catch(Exception ex) {
//        	ex.printStackTrace();
//        	return null;
//        }
//    }
//    
//    /**
//     * 마이크로 서비스 IF 대상 데이터를 조회하는 클라이언트 서비스
//     * 1) 발주인터페이스 : PID|agentID|userUID
//     *  
//     * @param Map<String,Object> rsMap 
//     * @return String results
//     */
//    private String getIFData4NoneIF(Map<String,Object> rsMap) {    	
//    	String jsonRet = null;
//    	try {
//    		
//    		jsonRet = this.channel.getIFData4NoneIF(rsMap, this.BUFFER_TYPE);
//    		
//    		if (jsonRet != null && jsonRet != "") {
//    			jsonRet = this.saveJsonFile(rsMap.get("pID").toString(), jsonRet);
//   			}
//    		
//    		this.channel.closeChannel();
//        	return jsonRet;
//        } catch(Exception ex) {
//        	ex.printStackTrace();
//        	return null;
//        }
//    }
//    
//    /**
//     * 수신한 데이터를 해당일자에 업무별 Json 파일로 저장함.
//     * 
//     * @param String infPID
//     * @param String jsonRet
//     * @return
//     * @throws Exception
//     */
//    private String saveJsonFile(String infPID, String jsonRet) throws Exception {
//    	JSONObject jsonObj = null;
//    	FileWriter jsonWriter = null;
//    	String folder   = null;
//    	String fileName = null;
//    	try {
//    		jsonObj = (JSONObject) new JSONParser().parse(jsonRet);
//    		String tmpTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System.currentTimeMillis());    		
//    		String yyyy = tmpTime.substring(0, 4);
//    		String MM   = tmpTime.substring(4, 6);
//    		String dd   = tmpTime.substring(6, 8);
//    		
//    		String filePath = "/"+ infPID +"/"+ yyyy +"/"+ MM +"/"+ dd;
//    		folder = this.JSON_PATH + filePath;
//    		
//    		new File(folder).mkdirs();
//    		
//    		fileName = filePath +"/"+ tmpTime +".json";
//    		
//    		jsonWriter = new FileWriter(folder +"/"+ tmpTime +".json");
//    		jsonWriter.write(jsonObj.toJSONString());
//    		jsonWriter.flush();
//    		jsonWriter.close();
//     
//    		return fileName; 
//    	} catch (IOException e) {
//    		throw e;
//    	} catch (Exception e) {
//    		throw e;
//    	} finally {
//    		if (jsonObj != null) { jsonObj = null; }
//    		if (jsonWriter != null) { 
//        		try { jsonWriter.flush(); } catch (Exception e1) { }
//        		try { jsonWriter.close(); } catch (Exception e1) { }
//    			jsonWriter = null; 
//    		}
//    	}
//    }
    
    /**
     * 계약요청 싱크용 스케줄러 : TSRC와 같은 DB를 사용해야 함. 실시간 상태 변경 처리가 너무 난해함.
     *       
    @Scheduled(fixedDelayString="${mst.contReq.fixedDelay}")
    public void runner4ContReqDetail() {
    	long start = System.currentTimeMillis();
    	long end   = 0;
    	logger.info("==========Remote "+ this.MST_TSRC_URI +" ContReq Call Begin============="+ new Date());    	
    	String svcKey = com.sci4s.util.Config.SVC_KEY.toUpperCase();
    	String jsonRet = null;
    	List<Map<String,Object>> rsList = null;
    	try {
    		if (this.IS_SYNC_RUNNING) {
    			String ifUID = UUID.randomUUID().toString();
    		
    			MsaApiGrpc.MsaApiBlockingStub stub = MsaApiGrpc.newBlockingStub(this.channel.openChannel(MST_TSRC_URI));        	
            	SciRIO.ReqMsg reqMsg = SciRIO.ReqMsg.newBuilder()
            			.setAgentID(this.AGENT_ID)
        				.setMsg(ifUID)
        				.build();
            	
            	RetMsg retMsg = stub.getTblContReqDetail(reqMsg);        	
        		if (retMsg.getErrCode().equals("0")) {
        			if (retMsg.getResults().indexOf("NO_DATA") >= 0) {
        				jsonRet = null;
        			} else {
        				jsonRet = retMsg.getResults();
        			}
        		}
        		this.channel.closeChannel();
        		
	    		if (jsonRet != null) { // 데이터를 저장함
	        		JSONObject jsonObj = (JSONObject) new JSONParser().parse(jsonRet);	
	        		rsList = new ObjectMapper().readValue(jsonObj.get("results").toString(), new TypeReference<List<Map<String,Object>>>(){});
	        		if (rsList != null) { 
	        			tsyncService.saveTblContReqDetailAll(rsList); 
	        		}
		    	}
    		} else {
    			logger.info("IS_SYNC_RUNNING ::: "+ this.SYNC_RUNNING);
    		}
    		end = System.currentTimeMillis();
    	} catch(Exception ex) {
         	ex.printStackTrace();
        } finally {
         	if (rsList != null) { rsList = null; }
        }
    	logger.info("==========Remote "+ this.MST_TSRC_URI +" ContReq Call Finish============="+ ( end - start ) +"ms");
    }
    */
}
