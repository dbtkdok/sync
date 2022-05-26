package com.sci4s.grpc.batch;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sci4s.err.ThrowException;
import com.sci4s.grpc.ErrConstance;
import com.sci4s.grpc.svc.Channel;
import com.sci4s.grpc.svc.TSyncService;
import com.sci4s.grpc.svc.tsys.TSysService;

//@RefreshScope
//@Component
public class RunnableTask implements Runnable {
	Logger logger = LoggerFactory.getLogger(RunnableTask.class);

	private Map<String,List<String>> targetMap;
	private Map<String,String> funtionMap;

	private String rpid;
	private String rifPID;
    private String rmsaID;
    private List<String> rmsaIDs;
    private String rfunctionNM;
    private Map<String,Object> commInfoMap;
    private String rtype;
  
    private TSysService  tsysService;
	private Channel channel;
	private TSyncService tsyncService;
    
    public RunnableTask(String rpid, String rifPID, String rmsaID, List<String> rmsaIDs
    		, String rfunctionNM, Map<String, Object> commInfoMap, String rtype
    		, Map<String,List<String>> targetMap, Map<String,String> funtionMap
    		, TSyncService tsyncService, TSysService tsysService, Channel channel){
        this.rpid = rpid;
        this.rifPID = rifPID;
        this.rmsaID = rmsaID;
        this.rmsaIDs = rmsaIDs;
        this.rfunctionNM = rfunctionNM;
        this.commInfoMap = commInfoMap;
        this.rtype = rtype;
        this.targetMap = targetMap;
        this.funtionMap = funtionMap;
        this.channel = channel;
		this.tsyncService = tsyncService;
		this.tsysService  = tsysService;
    }
    
    @Override
    public void run() {
    	if("syncCommon4XmlCreate".equals(rtype)) {
    		this.syncCommon4XmlCreate(rpid, rifPID, rmsaID, rmsaIDs, rfunctionNM, commInfoMap);
    	} else if("sync".equals(rtype)) {
    		this.procTblSyncJobRequest();
    	} else if("hist".equals(rtype)) {
    		this.procTblSyncJobHist();
    	} else {
    		this.syncCommon4NoneIF(rpid, rifPID, rmsaID, rfunctionNM);
    	}
    }
    
    public void syncCommon4XmlCreate(String pid, String ifPID, String msaID, List<String> msaIDs, String functionNM, Map<String,Object> commInfoMap) {
    	long start = System.currentTimeMillis();
    	long end = 0;
//    	logger.info("pid ::: " + pid + " _ ifPID ::: " + ifPID + " _ msaID ::: " + msaID + " _ msaIDs ::: " + msaIDs + " _ functionNM ::: " + functionNM + " _ rsList ::: " + rsList);
    	logger.info("==========Remote "+ commInfoMap.get(msaID) +" "+ pid +" Call Begin============="+ new Date());
    	boolean sync_running = Boolean.parseBoolean("" + commInfoMap.get("SYNC_RUNNING"));
    	if (sync_running) {
    		Map<String,Object> rsMap = new HashMap<String,Object>();
			rsMap.putAll(commInfoMap);
			rsMap.put("pID",    pid);
			rsMap.put("ifPID",  ifPID);
			rsMap.put("ifRSTS", "N");
			rsMap.put("ifMSA",  ""+ commInfoMap.get(msaID));
			rsMap.put("msaID",  ""+ msaID);
    		try {
    			tsysService.insTblSyncJobHist(commInfoMap);    
    			
    			long ifUID = 0;
    			if (rsMap.containsKey("ifUID")) {
    				ifUID = Long.parseLong(rsMap.get("ifUID").toString());
    			}
    			logger.info("ifUID============="+ ifUID); 
    			
    			rsMap.put("ifUID", ifUID);
    			
				tsyncService.saveXmlCreate(msaIDs, functionNM, commInfoMap);
				rsMap.put("ifRSTS", "Y");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rsMap.put("ifRSTS", "E");
			}
    		
    		try {tsysService.updTblSyncJobHist(commInfoMap);} catch (Exception e) {}
	    	end = System.currentTimeMillis();
		}  else {
			logger.info("IS_SYNC_RUNNING ::: "+ sync_running);
		}
    	
    	logger.info("==========Remote "+ pid +" Call Finish============="+ ( end - start ) +"ms");
    }
    
    public void syncCommon4NoneIF(String pid, String ifPID, String msaID, String methodNM) {
    	long start = System.currentTimeMillis();
    	long end   = 0;
//    	logger.info("pid ::: " + pid + " _ ifPID ::: " + ifPID + " _ msaID ::: " + msaID + " _ methodNM ::: " + methodNM);
    	logger.info("==========Remote "+ commInfoMap.get(msaID) +" "+ pid +" Call Begin============="+ new Date());    	
    	String svcKey = com.sci4s.util.Config.SVC_KEY.toUpperCase();
    	List<Map<String,Object>> rsList = null;
    	Map<String,Object> rsMap = null;
    	boolean sync_running = Boolean.parseBoolean("" + commInfoMap.get("SYNC_RUNNING"));
    	
    	try {
    		if (sync_running) {
//    		if (sync_running && pid.equals("SYNC0012")) {
    			// select pID, pNM, tableNM, ifMSA, ifParamIDs, targets, functionNM 
    			//   from tbl_syncjob 
    			//  where agentID = #{agentID} and pID = #{pID}
    			rsMap = new HashMap<String,Object>();
    			rsMap.putAll(commInfoMap);
    			rsMap.put("pID",    pid);
    			rsMap.put("ifPID",  ifPID);
    			rsMap.put("ifRSTS", "N");
    			rsMap.put("ifMSA",  ""+ commInfoMap.get(msaID));
    			rsMap.put("msaID",  ""+ commInfoMap.get(msaID));
    			rsMap.put("syncType",  "");
    			
    			logger.info("rsMap :::::::::::: "+ rsMap);
    			rsList = tsyncService.getTblSyncJobList(rsMap);
    			logger.info("rsList :::::::::::: "+ rsList);
    			String targets  = "";
    			if (rsList != null && rsList.size() > 0) {
    				String type  = ""+ rsList.get(0).get("type");
    				targets  = ""+ rsList.get(0).get("targets");
    				String ifParamIDs  = ""+ rsList.get(0).get("ifParamIDs");
    				String ifParamVals = ifParamIDs;
    				String[] ids = null;
    				if (ifParamIDs.indexOf("|") >= 0) {
    					ids = ifParamIDs.split("\\|");
    				} else {
    					ids = new String[] { ifParamIDs };
    				}
    				for (int ii=0; ii<ids.length; ii++) {
    					logger.info("ids :::::::::::: "+ ids[ii]);
    					
    					if ("tblName".equals(ids[ii])) {
    						ifParamVals = ifParamVals.replace(ids[ii], ""+ rsList.get(0).get("tableNM"));
    					} else if ("svcKey".equals(ids[ii])) {
    						ifParamVals = ifParamVals.replace(ids[ii], svcKey);
    					} else if ("PID".equals(ids[ii])) {
    						ifParamVals = ifParamVals.replace(ids[ii], ifPID);
    					} else {
    						ifParamVals = ifParamVals.replace(ids[ii], ""+ commInfoMap.get(ids[ii]));
    					}
    				}
    				logger.info("ifParamVals :::::::::::: "+ ifParamVals);
    				rsMap.put("type", type);
    				rsMap.put("targets", targets);
    				rsMap.put("pNM",        rsList.get(0).get("pNM"));
    				rsMap.put("tableNM",    rsList.get(0).get("tableNM"));
    				rsMap.put("methodNM",   methodNM);
    				rsMap.put("svcKey",     svcKey);
    				rsMap.put("ifParamIDs", ifParamIDs);
    				rsMap.put("ifParamVals",ifParamVals);  
    				rsMap.put("syncType",  "get");
    			}
    			
    			tsysService.insTblSyncJobHist(rsMap);    			
    			long ifUID = 0;
    			if (rsMap.containsKey("ifUID")) {
    				ifUID = Long.parseLong(rsMap.get("ifUID").toString());
    			}
    			logger.info("ifUID============="+ ifUID); 
    			
    			rsMap.put("ifUID", ifUID);

    			String jsonFileNM = null;
    			if ("getIFData4NoneIF".equals(methodNM)) {
    				jsonFileNM = this.getIFData4NoneIF(rsMap);
    			} else {
    				jsonFileNM = this.syncMstInfoTsys(rsMap);
    			}
	    		if (jsonFileNM != null) { // 데이터를 저장함
	    			if("fileCrateFalse".equals(jsonFileNM)) {
	    				rsMap.put("jsonFileNM", jsonFileNM);
		    			rsMap.put("ifRSTS", "E");
		    			rsMap.put("results", "1) a json file is not create.");
	    			} else {
	    				rsMap.put("jsonFileNM", jsonFileNM);
		    			rsMap.put("ifRSTS", "Y");
		    			rsMap.put("ifPSTS", "Y");
		    			rsMap.put("results", "1) a json file is create successfully.");
	    			}
	    		} else {
	    			rsMap.put("ifRSTS", "Y");
	    			rsMap.put("results", "1) no data");
	    		}
	    		tsysService.updTblSyncJobHist(rsMap);
	    		
	    		if (jsonFileNM != null) { 
	    			if(!"fileCrateFalse".equals(jsonFileNM)) {
		    			rsMap.put("ifPSTS", "T");
		    			
		    			String[] target = null;
			    		if (targets.indexOf("|") >= 0) {
			    			target = targets.split("\\|");
						} else {
							target = new String[] { targets };
						}
			    		
			    		tsysService.insTblSyncJobRequest(rsMap, target);
	    			}
	    		} 
    		} else {
    			logger.info("IS_SYNC_RUNNING ::: "+ sync_running);
    		}
    		end = System.currentTimeMillis();
    	} catch(Exception ex) {
    		if (sync_running) { 
    			rsMap.put("ifRSTS", "E");
    			rsMap.put("results", new ThrowException(ErrConstance.getPrintStackTrace(ex)).getErrMsg());
	    		try {	tsysService.updTblSyncJobHist(rsMap); } catch(Exception e0) { }
    		}
        } finally {
         	if (rsList != null) { rsList = null; }
         	if (rsMap  != null) { rsMap  = null; }
        }
    	logger.info("==========Remote "+ commInfoMap.get(msaID) +" "+ pid +" Call Finish============="+ ( end - start ) +"ms");
    }
    
    private String syncMstInfoTsys(Map<String,Object> rsMap) {    	
    	try {
    		String tblNM = rsMap.get("tableNM").toString();
        	String jsonRet = null;
        	String type = "";
        	rsMap.put("CS_KEY", commInfoMap.get("CS_KEY"));
        	rsMap.put("userUID", commInfoMap.get("userUID"));
        	rsMap.put("BORG_UID", commInfoMap.get("BORG_UID"));
        	rsMap.put("agentID", commInfoMap.get("agentID"));
        	rsMap.put("MSA_IP", commInfoMap.get("MSA_IP"));
        	rsMap.put("GRPC_URI", rsMap.get("ifMSA").toString());
        	rsMap.put("tableNM", tblNM);
        	
        	if ("tbl_codes".equals(tblNM) || "tbl_attach".equals(tblNM) || "tbl_rolescopesif".equals(tblNM)
        			 || "tbl_rolemenuif".equals(tblNM)	|| "tbl_dictionaryif".equals(tblNM) || "tbl_cmcodes".equals(tblNM)
	        		) {
	   			type = "DATA";
	   		}
//        	logger.info("type :::: " + type);
        	jsonRet = this.channel.syncMstInfoTsys(rsMap, (String) commInfoMap.get("BUFFER_TYPE"), type);
        	
        	if (jsonRet != null && jsonRet != "") {
        		jsonRet = this.saveJsonFile(rsMap.get("pID").toString(), jsonRet);
   			}
        	
    		this.channel.closeChannel();
        	return jsonRet;
        } catch(Exception ex) {
        	ex.printStackTrace();
        	return null;
        }
    }
    
    /**
     * 마이크로 서비스 IF 대상 데이터를 조회하는 클라이언트 서비스
     * 1) 발주인터페이스 : PID|agentID|userUID
     *  
     * @param Map<String,Object> rsMap 
     * @return String results
     */
    private String getIFData4NoneIF(Map<String,Object> rsMap) {    	
    	String jsonRet = null;
    	try {
    		
    		jsonRet = this.channel.getIFData4NoneIF(rsMap, (String) commInfoMap.get("BUFFER_TYPE"));
    		
    		if (jsonRet != null && jsonRet != "") {
    			jsonRet = this.saveJsonFile(rsMap.get("pID").toString(), jsonRet);
   			}
    		
    		this.channel.closeChannel();
        	return jsonRet;
        } catch(Exception ex) {
        	ex.printStackTrace();
        	return null;
        }
    }
    
    /**
     * 수신한 데이터를 해당일자에 업무별 Json 파일로 저장함.
     * 
     * @param String infPID
     * @param String jsonRet
     * @return
     * @throws Exception
     */
    private String saveJsonFile(String infPID, String jsonRet) throws Exception {
    	JSONObject jsonObj = null;
    	FileWriter jsonWriter = null;
    	String folder   = null;
    	String fileName = null;
    	try {
    		jsonObj = (JSONObject) new JSONParser().parse(jsonRet);
    		String tmpTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System.currentTimeMillis());    		
    		String yyyy = tmpTime.substring(0, 4);
    		String MM   = tmpTime.substring(4, 6);
    		String dd   = tmpTime.substring(6, 8);
    		
    		String filePath = "/"+ infPID +"/"+ yyyy +"/"+ MM +"/"+ dd;
    		folder = commInfoMap.get("JSON_PATH") + filePath;
    		
    		new File(folder).mkdirs();
    		
    		fileName = filePath +"/"+ tmpTime +".json";
    		
    		jsonWriter = new FileWriter(folder +"/"+ tmpTime +".json");
    		jsonWriter.write(jsonObj.toJSONString());
    		jsonWriter.flush();
    		jsonWriter.close();
     
    		return fileName; 
    	} catch (IOException e) {
    		fileName = "fileCrateFalse";
    		return fileName;
    	} catch (Exception e) {
    		fileName = "fileCrateFalse";
    		return fileName;
    	} finally {
    		if (jsonObj != null) { jsonObj = null; }
    		if (jsonWriter != null) { 
        		try { jsonWriter.flush(); } catch (Exception e1) { }
        		try { jsonWriter.close(); } catch (Exception e1) { }
    			jsonWriter = null; 
    		}
    	}
    }
    
    /**
     * 전체 데이터 싱크 처리용 스케줄러
     *  
     */ 
    public void procTblSyncJobHist() {
    	long start = System.currentTimeMillis();
    	long end   = 0;
    	
    	List<Map<String,Object>> retList = null;
    	Map<String,Object> rsMap = new HashMap<String, Object>();
    	logger.info("==========Remote "+ commInfoMap.get("tsys") +" SyncJobHist Call Begin============="+ new Date());
    	boolean sync_running = Boolean.parseBoolean("" + commInfoMap.get("SYNC_RUNNING"));
    	try {
//    		logger.info("commInfoMap ::: " + commInfoMap + " _ funtionMap ::: " + funtionMap + " _ targetMap ::: " + targetMap);
    		if (sync_running) {
    			retList = tsysService.getTblSyncJobHist(rsMap);   
    			String targets = "";
    			if (retList != null && retList.size() > 0) {
    				for (Map<String,Object> retMap : retList) { 
    					try {
	    					retMap.putAll(commInfoMap);
	    					retMap.put("ifRSTS", "N");
	    					retMap.put("ifMSA",  ""+ commInfoMap.get(retMap.get("msa")));
	    					retMap.put("syncType",  "sync");
	    					
	    					String pID  = ""+ retMap.get("pID");
	    					String methodNM  = ""+ retMap.get("methodNM");
	    					String type  = ""+ retMap.get("type");
	    					targets  = ""+ retMap.get("targets");
	    					String ifParamIDs  = ""+ retMap.get("ifParamIDs");
	        				String ifParamVals = ""+ retMap.get("ifParamVals");
	        				String[] ids = null;
	        				String[] vals = null;
	        				if(ifParamIDs != null) {
	        					if (ifParamIDs.indexOf("|") >= 0) {
	            					ids = ifParamIDs.split("\\|");
	            					vals = ifParamVals.split("\\|");
	            				} else {
	            					ids = new String[] { ifParamIDs };
	            					vals = new String[] { ifParamVals };
	            				}
	            				
	            				for (int ii=0; ii<ids.length; ii++) {
	            					retMap.put(ids[ii],   vals[ii]);
	            				}
	        				}
	        				
	    					if("syncCommon4XmlCreate".equals(type)) {
	    						String funcName = funtionMap.get(pID);   
				        		List<String> target = targetMap.get(pID);
	    						
	    						tsyncService.saveXmlCreate(target, funcName, retMap);
	    						
	    						retMap.put("ifRSTS", "Y");
	    						tsysService.updTblSyncJobHist(retMap);
	    					} else {
	            				String jsonFileNM = null;
	            				if ("getIFData4NoneIF".equals(methodNM)) {
	                				jsonFileNM = this.getIFData4NoneIF(retMap);
	                			} else {
	                				jsonFileNM = this.syncMstInfoTsys(retMap);
	                			}
	            				if (jsonFileNM != null) { // 데이터를 저장함
	            					retMap.put("jsonFileNM", jsonFileNM);
	            					retMap.put("ifRSTS", "Y");
	            					retMap.put("ifPSTS", "Y");
	            					retMap.put("results", "1) a json file is create successfully.");
	            	    		} else {
	            	    			retMap.put("ifRSTS", "Y");
	            	    			retMap.put("results", "1) no data");
	            	    		}
	            				
	            	    		tsysService.updTblSyncJobHist(retMap);
	            	    		if (jsonFileNM != null) { 
	            	    			if(!"fileCrateFalse".equals(jsonFileNM)) {
	            		    			rsMap.put("ifPSTS", "T");
	            		    			
	            		    			String[] target = null;
	            			    		if (targets.indexOf("|") >= 0) {
	            			    			target = targets.split("\\|");
	            						} else {
	            							target = new String[] { targets };
	            						}
	            			    		
	            			    		tsysService.insTblSyncJobRequest(rsMap, target);
	            	    			}
	            	    		} 
	    					}
    					} catch(Exception ex) {
    						if (sync_running) { 
    							retMap.put("ifRSTS", "E");
    							retMap.put("results", new ThrowException(ErrConstance.getPrintStackTrace(ex)).getErrMsg());
    			    			try { tsysService.updTblSyncJobHist(rsMap); } catch(Exception e0) { }
    			    		}
    					}
    				}
    			}
    		} else {
				logger.info("IS_SYNC_RUNNING ::: "+ sync_running);
			}
    		end = System.currentTimeMillis();
		} catch(Exception ex) {
			ex.printStackTrace();
	    } finally {
	     	if (retList != null) { retList = null; }
	     	if (rsMap != null) { rsMap = null; }
	    }
    	logger.info("==========Remote "+ commInfoMap.get("tsys") +" SyncJobHist Call Finish============="+ ( end - start ) +"ms");
    }
    
    
    /**
     * 전체 데이터 싱크 처리용 스케줄러
     *  
     */ 
    public void procTblSyncJobRequest() {
    	long start = System.currentTimeMillis();
    	long end   = 0;
    	logger.info("==========Remote "+ commInfoMap.get("tsys") +" SyncJobHist Call Begin============="+ new Date());    	
    	List<Map<String,Object>> retList = null;
    	Map<String,Object> rsMap = null;
    	boolean sync_running = Boolean.parseBoolean("" + commInfoMap.get("SYNC_RUNNING"));
    	try {
//    		logger.info("commInfoMap ::: " + commInfoMap + " _ funtionMap ::: " + funtionMap + " _ targetMap ::: " + targetMap);
    		if (sync_running) {
    			rsMap = new HashMap<String,Object>();
    			
    			rsMap.put("agentID",     commInfoMap.get("agentID"));
    			rsMap.put("userUID",     commInfoMap.get("USER_UID"));
    			
//    			retList = tsysService.getTblSyncJobHist(rsMap);    	
    			retList = tsysService.getTblSyncJobRequest(rsMap);   
    			if (retList != null && retList.size() > 0) {
    				for (Map<String,Object> retMap : retList) {    					
//    					String ifUID      = ""+ retMap.get("ifUID");
    					String reqUID      = ""+ retMap.get("reqUID");
    					String pID        = ""+ retMap.get("pID");
    					String results    = ""+ retMap.get("results");
    					String jsonFileNM = ""+ retMap.get("jsonFileNM");
    					String targets = ""+ retMap.get("targets");
    					
//    					rsMap.put("ifUID", ifUID);
    					rsMap.put("reqUID", reqUID);
    					rsMap.put("procStartDT", "Y");
//    					try { tsysService.updTblSyncJobHist(rsMap); } catch(Exception e0) { } //실행시작시간
    					try { tsysService.updTblSyncJobRequest(rsMap); } catch(Exception e0) { } //실행시작시간
    					try {
    						String fileFullPath = commInfoMap.get("JSON_PATH") + jsonFileNM;
    						
    						logger.info("fileFullPath ::::::::::::"+ fileFullPath);
    						
    						
   			        		JSONObject jsonObj = (JSONObject) new JSONParser().parse(new FileReader(fileFullPath));	
   			        		List<Map<String,Object>> rsList = new ObjectMapper().readValue(jsonObj.get("results").toString(), new TypeReference<List<Map<String,Object>>>(){});
   			        		if (rsList != null) {
   			        			String funcName = funtionMap.get(pID);   
   			        			List<String> target = new ArrayList<String>();
   			        			target.add(targets);
		    					if (pID.equals("SCM4130")) { // 일반계약발주
		    						tsyncService.saveTblPoDataAll(rsList);
		    					} else {
		    						Class prmTypes[] = { List.class, String.class, List.class };
		    						Method method = tsyncService.getClass().getDeclaredMethod(funcName, prmTypes);
		    						method.invoke(tsyncService, new Object[] { target, funcName, rsList });		
		    					}
		    					
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
   			        		}
	    					rsMap.put("ifPSTS", "Y");
//	    					rsMap.put("results", results +"\n"+ "2) "+ retMap.get("pNM") +" 완료");
    						rsMap.put("results", "1) "+ retMap.get("pNM") +" 완료");
    					} catch(Exception ex) {
    						rsMap.put("ifPSTS", "E");
    						String errMsg = new ThrowException(ErrConstance.getPrintStackTrace(ex)).getErrMsg();
    						
    						rsMap.put("results", "1) "+ errMsg);
    			        }
    					rsMap.put("procEndDT", "Y");
//    					try { tsysService.updTblSyncJobHist(rsMap); } catch(Exception e0) { }
    					try { tsysService.updTblSyncJobRequest(rsMap); } catch(Exception e0) { }
    				}
    			}
    		}
    		end = System.currentTimeMillis();
    	} catch(Exception ex) {
    		ex.printStackTrace();
        } finally {
         	if (retList != null) { retList = null; }
         	if (rsMap != null) { rsMap = null; }
        }
    	logger.info("==========Remote "+ commInfoMap.get("tsys") +" SyncJobHist Call Finish============="+ ( end - start ) +"ms");
    }
}