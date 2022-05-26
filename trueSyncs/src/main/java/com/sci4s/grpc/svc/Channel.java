package com.sci4s.grpc.svc;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.flatbuffers.FlatBufferBuilder;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.sci4s.fbs.Data;
import com.sci4s.fbs.FlatJsonGrpc;
import com.sci4s.fbs.ReqMsg;
import com.sci4s.fbs.RetMsg;
import com.sci4s.grpc.MsaApiGrpc;
import com.sci4s.grpc.SciRIO;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

@Service
public class Channel extends RpcChannel {
	
	private Logger logger = LoggerFactory.getLogger(Channel.class);
	
	private long TIME_OUT = 5000;
	
	private EurekaClient eurekaClient = null;
	
	@PostConstruct
	public void init() {
		System.setProperty("eureka.preferSameZone", "true");
		System.setProperty("eureka.shouldUseDns", "false");
		System.setProperty("eureka.serviceUrl.default", "http://admin:qwer1234@www.sci4s.com:8890/eureka/");
		System.setProperty("eureka.client.serviceUrl.defaultZone", "http://admin:qwer1234@www.sci4s.com:8890/eureka/");

		DiscoveryManager.getInstance().initComponent(new MyDataCenterInstanceConfig(), new DefaultEurekaClientConfig());
		try {
			this.eurekaClient = DiscoveryManager.getInstance().getEurekaClient();
		} catch(Exception ex) {
			logger.error("@@@@@@@@@@BaseChannel@@@@@@@@@@@");
			ex.printStackTrace();
			logger.error("@@@@@@@@@@BaseChannel@@@@@@@@@@@");
		}
    }
	
	
	/**
	 * GRPC 채널 오픈
	 * 
	 * @return ManagedChannel
	 */
	public ManagedChannel openChannel(String chnl) throws Exception {
		String GRPC_URI = "";		
//		String[] rets = new String[2];
//		String appName = "";
//		if("thr".equals(chnl)){
//		  appName = "TRUEHR";
//		} else if("tsys".equals(chnl)) {
//		  appName = "TRUESYS";
//		}else if("tbbs".equals(chnl)) {
//		  appName="TRUEBBS";
//		}else if("tcms".equals(chnl)) {
//		  appName="TRUECMS";
//		}else if("tsrc".equals(chnl)) {
//		  appName="TRUESRC";
//		}else if("tcont".equals(chnl)) {
//		  appName="TRUECONT";
//		}else if("tscm".equals(chnl)) {
//		  appName="TRUESCM";
//		}else if("tcims".equals(chnl)) {
//		  appName="TRUECIMS";
//		}
//		System.out.println("chnl :::: " + chnl + " ---- appName :::: " + appName);
//		
//		InstanceInfo instInfo = this.eurekaClient.getNextServerFromEureka(appName, false);
//		rets[0] = instInfo.getIPAddr();
//		rets[1] = ""+ instInfo.getPort();
//		
//		GRPC_URI = rets[0] + ":" + rets[1];
		GRPC_URI = chnl;
    	logger.info("Channel["+ GRPC_URI +"]");
    	
		try {
			this.channel = ManagedChannelBuilder.forTarget(GRPC_URI)
							.usePlaintext()
							.directExecutor()
							.maxInboundMessageSize(2147483647)
							.build();
			
			return this.channel;
		} catch (Exception e){
		    e.printStackTrace();
		    throw e;
		}
	}
	
	public String syncMstInfoTsys(Map<String, Object> rsMap, String buffertype, String type) throws Exception {
		if ("PROTO".equals(buffertype)) {
			return syncMstInfoTsysProto(rsMap, type);
		} else {		
			return syncMstInfoTsysFlat(rsMap, type);
		}
	}
	
	public String syncMstInfoTsysFlat(Map<String, Object> rsMap, String type) throws Exception {
		String msaID = (String) rsMap.get("msaID");

		FlatBufferBuilder builder = new FlatBufferBuilder();
		FlatJsonGrpc.FlatJsonBlockingStub stub = FlatJsonGrpc.newBlockingStub(this.openChannel(msaID));
		String jsonRet = null;
		
		Map<String, Object> grpcMap = new HashMap<String, Object>();	
    	grpcMap.put("agentID", rsMap.get("agentID").toString());
		grpcMap.put("tblName", rsMap.get("tableNM").toString());
		grpcMap.put("svcKey",  rsMap.get("svcKey").toString());
		grpcMap.put("syncType",  rsMap.get("syncType").toString());
		
		try {		
			if("DATA".equals(type)) {
				Data fbsReq = null;
				RetMsg fbsResp = null;
				Map<String, Object> retMap = null;
						
					builder = new FlatBufferBuilder();
					int dataOffset = Data.createData(builder
							, builder.createString("" + rsMap.get("ifPID").toString())
							, builder.createString("" + rsMap.get("agentID").toString())
							, builder.createString("" + rsMap.get("CS_KEY").toString())
							, builder.createString("" + rsMap.get("MSA_IP").toString())
							, builder.createString("" + rsMap.get("MSA_IP").toString())
							, builder.createString("" + rsMap.get("userUID").toString())
							, builder.createString("" + rsMap.get("BORG_UID").toString())
							, builder.createString("KR")
							, builder.createString("" + com.sci4s.util.JsonUtil.getJsonStringFromMapHead("params", grpcMap))
							, builder.createString("0")
							, builder.createString(""));
					builder.finish(dataOffset);        
					com.sci4s.fbs.Data reqMap = com.sci4s.fbs.Data.getRootAsData(builder.dataBuffer());
					com.sci4s.fbs.RetMsg retMsg = stub.callRMsg(reqMap);
					
					if (retMsg.errCode().equals("0")) {
		       			if (retMsg.results().indexOf("NO_DATA") >= 0) {
		       				jsonRet = null;
		       			} else {
		       				jsonRet = retMsg.results();  				
		       			}
		       		}
			} else {
				int dataOffset = ReqMsg.createReqMsg(builder
						, builder.createString(rsMap.get("agentID").toString())
						, builder.createString(rsMap.get("tableNM").toString() +"|"+ rsMap.get("svcKey").toString() +"|"+ rsMap.get("syncType").toString()));
			
				builder.finish(dataOffset);        
				com.sci4s.fbs.ReqMsg reqMap = com.sci4s.fbs.ReqMsg.getRootAsReqMsg(builder.dataBuffer());
				
				com.sci4s.fbs.RetMsg retMsg = stub.getMstInfo(reqMap);
   	    		if (retMsg.errCode().equals("0")) {
   	    			if (retMsg.results().indexOf("NO_DATA") >= 0) {
           				jsonRet = null;
           			} else {
           				jsonRet = retMsg.results();
           			}
   	    		}
			}
		
			return jsonRet;
		} catch(StatusRuntimeException ex) {
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.syncMstInfoTsysFlat.StatusRuntimeException.S%%%%%%%%%%%");
			ex.printStackTrace();
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.syncMstInfoTsysFlat.StatusRuntimeException.E%%%%%%%%%%%");
			throw ex;
		} catch(Exception ex) {
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.syncMstInfoTsysFlat.Exception.S%%%%%%%%%%%");
			ex.printStackTrace();
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.syncMstInfoTsysFlat.Exception.E%%%%%%%%%%%");
			throw ex;
		} finally {   
			if (this.channel != null)  { 
				try { this.closeChannel();} catch(Exception e) { }
			}
	    	if (builder  != null) { builder = null; }
	    	if (stub  != null) { stub = null; }
	    	if (rsMap  != null) { rsMap = null; }
	    	if (jsonRet  != null) { jsonRet = null; }
	    }
	}
	
	public String syncMstInfoTsysProto(Map<String, Object> rsMap, String type) throws Exception {
		String jsonRet = null;
		String msaID = (String) rsMap.get("msaID");
		
		MsaApiGrpc.MsaApiBlockingStub stub = MsaApiGrpc.newBlockingStub(this.openChannel(msaID));
		
		Map<String, Object> grpcMap = new HashMap<String, Object>();	
    	grpcMap.put("agentID", rsMap.get("AGENT_ID").toString());
		grpcMap.put("tblName", rsMap.get("tableNM").toString());
		grpcMap.put("svcKey",  rsMap.get("svcKey").toString());
		grpcMap.put("syncType",  rsMap.get("syncType").toString());
		
		try {		
			if("DATA".equals(type)) {
				SciRIO.Data reqMap = SciRIO.Data.newBuilder()
           				.setPID(rsMap.get("ifPID").toString())
           				.setCsKey(rsMap.get("CS_KEY").toString())
           				.setData(com.sci4s.util.JsonUtil.getJsonStringFromMapHead("params", grpcMap))
           				.setUserIP(rsMap.get("MSA_IP").toString())
           				.setServerIP(rsMap.get("MSA_IP").toString())
           				.setUserUID(rsMap.get("userUID").toString())
           				.setBorgUID(rsMap.get("BORG_UID").toString())
           				.setAgentID(rsMap.get("AGENT_ID").toString())
//           				.putAllParams(grpcMap)
           				.build();

				com.sci4s.grpc.SciRIO.RetMsg retMsg = stub.callRMsg(reqMap);
           		if (retMsg.getErrCode().equals("0")) {
           			if (retMsg.getResults().indexOf("NO_DATA") >= 0) {
           				jsonRet = null;
           			} else {
           				jsonRet = retMsg.getResults();      				
           			}
           		}
			} else {
				SciRIO.ReqMsg reqMsg = SciRIO.ReqMsg.newBuilder()
   	        			.setAgentID(rsMap.get("AGENT_ID").toString())
   	    				.setMsg(rsMap.get("tableNM").toString() +"|"+ rsMap.get("svcKey").toString() +"|"+ rsMap.get("syncType").toString())
   	    				.build();
   	        	
   	        	com.sci4s.grpc.SciRIO.RetMsg retMsg = stub.getMstInfo(reqMsg);
   	    		if (retMsg.getErrCode().equals("0")) {
   	    			if (retMsg.getResults().indexOf("NO_DATA") >= 0) {
           				jsonRet = null;
           			} else {
           				jsonRet = retMsg.getResults();
           			}
   	    		}
			}
		
			return jsonRet;
		} catch(StatusRuntimeException ex) {
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.syncMstInfoTsysProto.StatusRuntimeException.S%%%%%%%%%%%");
			ex.printStackTrace();
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.syncMstInfoTsysProto.StatusRuntimeException.E%%%%%%%%%%%");
			throw ex;
		} catch(Exception ex) {
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.syncMstInfoTsysProto.Exception.S%%%%%%%%%%%");
			ex.printStackTrace();
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.syncMstInfoTsysProto.Exception.E%%%%%%%%%%%");
			throw ex;
		} finally {
			if (this.channel != null)  { 
				try { this.closeChannel();} catch(Exception e) { }
			}
	    	if (stub  != null) { stub = null; }
	    	if (grpcMap  != null) { grpcMap = null; }
	    	if (jsonRet  != null) { jsonRet = null; }
	    }
	}
	
	public String getIFData4NoneIF(Map<String, Object> rsMap, String buffertype) throws Exception {
		if ("PROTO".equals(buffertype)) {
			return getIFData4NoneIFProto(rsMap);
		} else {		
			return getIFData4NoneIFFlat(rsMap);
		}
	}
	
	public String getIFData4NoneIFFlat(Map<String, Object> rsMap) throws Exception {
		String msaID = (String) rsMap.get("msaID");
		
		FlatBufferBuilder builder = new FlatBufferBuilder();
		FlatJsonGrpc.FlatJsonBlockingStub stub = FlatJsonGrpc.newBlockingStub(this.openChannel(msaID));
		String jsonRet = null;
		
		int dataOffset = ReqMsg.createReqMsg(builder
				, builder.createString(rsMap.get("agentID").toString())
				, builder.createString(rsMap.get("ifParamVals").toString() + "|" + rsMap.get("syncType").toString()));
	
		builder.finish(dataOffset);        
		com.sci4s.fbs.ReqMsg reqMap = com.sci4s.fbs.ReqMsg.getRootAsReqMsg(builder.dataBuffer());
		
		com.sci4s.fbs.RetMsg retMsg = stub.getIFData4NoneIF(reqMap);
		if (retMsg.errCode().equals("0")) {
			if (retMsg.results().indexOf("NO_DATA") >= 0) {
   				jsonRet = null;
   			} else {
   				jsonRet = retMsg.results();
   			}
		}
		
		return jsonRet;
	}
	
	public String getIFData4NoneIFProto(Map<String, Object> rsMap) throws Exception {
		String jsonRet = null;
		String GRPC_URI = (String) rsMap.get("ifMSA");
		
		MsaApiGrpc.MsaApiBlockingStub stub = MsaApiGrpc.newBlockingStub(this.openChannel(GRPC_URI));
		SciRIO.ReqMsg reqMsg = SciRIO.ReqMsg.newBuilder()
    			.setAgentID(rsMap.get("agentID").toString())
				.setMsg(rsMap.get("ifParamVals").toString() + "|" + rsMap.get("syncType").toString())
				.build();
		
		com.sci4s.grpc.SciRIO.RetMsg retMsg = stub.getIFData4NoneIF(reqMsg);
		if (retMsg.getErrCode().equals("0")) {
			if (retMsg.getResults().indexOf("NO_DATA") >= 0) {
				jsonRet = null;
			} else {
				jsonRet = retMsg.getResults();
			}
		}
		
		return jsonRet;
	}
	
	public String getSyncConfig(Map<String, Object> rsMap, String buffertype) throws Exception {
		if ("PROTO".equals(buffertype)) {
			return getSyncConfigProto(rsMap);
		} else {		
			return getSyncConfigFlat(rsMap);
		}
	}
	
	public String getSyncConfigFlat(Map<String, Object> rsMap) throws Exception {
		String msaID = ""+ rsMap.get("msaID");
		FlatBufferBuilder builder = new FlatBufferBuilder();
		FlatJsonGrpc.FlatJsonBlockingStub stub = FlatJsonGrpc.newBlockingStub(this.openChannel(msaID));
		String jsonRet = null;
		
		Map<String, Object> grpcMap = new HashMap<String, Object>();	
    	grpcMap.put("agentID", rsMap.get("agentID").toString());
		
		try {		
			Data fbsReq = null;
			RetMsg fbsResp = null;
			Map<String, Object> retMap = null;
				
			builder = new FlatBufferBuilder();
			int dataOffset = Data.createData(builder
					, builder.createString("TSY0116")
					, builder.createString("" + rsMap.get("agentID").toString())
					, builder.createString("")
					, builder.createString("127.0.0.1")
					, builder.createString("127.0.0.1")
					, builder.createString("" + rsMap.get("userUID").toString())
					, builder.createString("")
					, builder.createString("KR")
					, builder.createString("" + com.sci4s.util.JsonUtil.getJsonStringFromMapHead("params", grpcMap))
					, builder.createString("0")
					, builder.createString(""));
			builder.finish(dataOffset);        
			com.sci4s.fbs.Data reqMap = com.sci4s.fbs.Data.getRootAsData(builder.dataBuffer());
			com.sci4s.fbs.RetMsg retMsg = stub.callRMsg(reqMap);
			
			if (retMsg.errCode().equals("0")) {
       			if (retMsg.results().indexOf("NO_DATA") >= 0) {
       				jsonRet = null;
       			} else {
       				jsonRet = retMsg.results();  				
       			}
       		}
				
			return jsonRet;
		} catch(StatusRuntimeException ex) {
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.getSyncConfigFlat.StatusRuntimeException.S%%%%%%%%%%%");
			ex.printStackTrace();
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.getSyncConfigFlat.StatusRuntimeException.E%%%%%%%%%%%");
			throw ex;
		} catch(Exception ex) {
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.getSyncConfigFlat.Exception.S%%%%%%%%%%%");
			ex.printStackTrace();
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.getSyncConfigFlat.Exception.E%%%%%%%%%%%");
			throw ex;
		} finally {
			if (this.channel != null)  { 
				try { this.closeChannel();} catch(Exception e) { }
			}
	    	if (builder  != null) { builder = null; }
	    	if (stub  != null) { stub = null; }
	    	if (rsMap  != null) { rsMap = null; }
	    	if (jsonRet  != null) { jsonRet = null; }
	    }
	}
	
	public String getSyncConfigProto(Map<String, Object> rsMap) throws Exception {
		String msaID = "" +  rsMap.get("msaID");
		String jsonRet = null;
		MsaApiGrpc.MsaApiBlockingStub stub = MsaApiGrpc.newBlockingStub(this.openChannel(msaID));
		
		Map<String, Object> grpcMap = new HashMap<String, Object>();	
    	grpcMap.put("agentID", rsMap.get("agentID").toString());
		
		try {		
			SciRIO.Data reqMap = SciRIO.Data.newBuilder()
       				.setPID("TSY0116")
       				.setCsKey("")
       				.setData(com.sci4s.util.JsonUtil.getJsonStringFromMapHead("params", grpcMap))
       				.setUserIP("127.0.0.1")
       				.setServerIP("127.0.0.1")
       				.setUserUID(rsMap.get("userUID").toString())
       				.setBorgUID("")
       				.setAgentID(rsMap.get("agentID").toString())
       				.build();

			com.sci4s.grpc.SciRIO.RetMsg retMsg = stub.callRMsg(reqMap);
       		if (retMsg.getErrCode().equals("0")) {
       			if (retMsg.getResults().indexOf("NO_DATA") >= 0) {
       				jsonRet = null;
       			} else {
       				jsonRet = retMsg.getResults();      				
       			}
       		}
		
			return jsonRet;
		} catch(StatusRuntimeException ex) {
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.getSyncConfigProto.StatusRuntimeException.S%%%%%%%%%%%");
			ex.printStackTrace();
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.getSyncConfigProto.StatusRuntimeException.E%%%%%%%%%%%");
			throw ex;
		} catch(Exception ex) {
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.getSyncConfigProto.Exception.S%%%%%%%%%%%");
			ex.printStackTrace();
			logger.error(msaID +"%%%%%%%%%%GrpcChannel.getSyncConfigProto.Exception.E%%%%%%%%%%%");
			throw ex;
		} finally {
			if (this.channel != null)  { 
				try { this.closeChannel();} catch(Exception e) { }
			}
	    	if (stub  != null) { stub = null; }
	    	if (grpcMap  != null) { grpcMap = null; }
	    	if (jsonRet  != null) { jsonRet = null; }
	    }
	}
}
