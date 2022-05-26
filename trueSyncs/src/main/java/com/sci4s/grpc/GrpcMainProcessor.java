package com.sci4s.grpc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.sci4s.cnf.PIDSLoader;
import com.sci4s.grpc.SciRIO.ReqMsg;
import com.sci4s.grpc.SciRIO.RetMsg;
import com.sci4s.grpc.dto.GrpcParams;
import com.sci4s.grpc.dto.GrpcResp;
import com.sci4s.grpc.svc.CommService;
import com.sci4s.grpc.svc.TSyncService;
import com.sci4s.grpc.utils.GrpcDataUtil;

import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;

@Service
public class GrpcMainProcessor extends MsaApiGrpc.MsaApiImplBase implements BindableService {

	Logger logger = LoggerFactory.getLogger(GrpcMainProcessor.class);
	
	@Value("${tsys.db.dbtype}")
	String SQLMODE;
	
	@Value("${tsys.default.lang}")
	String CLANG;
	
	@Value("${msa.agentid}")
	String MSA_AGENTID;
	
	@Value("${msa.id}")
	String MSA_ID;

	@Value("${msa.pids.uri}")
	String TSYS_URI;
	
	@Value("${mst.chk.time}")
	String MST_CHKTIME;
	
	@Value("${buffer.type}")
	String BUFFER_TYPE;
	
	@Value("${msa.pids.buffer}")
	String MSA_BUFFER_TYPE;
	
	String xmlUri;
	
	private PIDSLoader pids;
	private ApplicationContext context;	
	private TSyncService tsyncService;
	private CommService commService;
	
	@Autowired
	public  GrpcMainProcessor(ApplicationContext context
			, TSyncService tsyncService, CommService commService){
	    this.context = context;
	    this.tsyncService = tsyncService;
	    this.commService  = commService;
	}
	
	@PostConstruct
    private void init() {
		this.xmlUri = TSYS_URI +"|"+ MSA_AGENTID +"|"+ MSA_ID;	    
	    if (pids == null) {
			try { pids = PIDSLoader.getInstance(this.xmlUri, this.MSA_BUFFER_TYPE); } catch(Exception ex) {}
		}
    }
	
	@Override
	public void callRMsg(SciRIO.Data request, StreamObserver<SciRIO.RetMsg> responseObserver) {
		logger.info("CALL "+ request.getPID() +" METHOD ###################################### START");
		String results = "";
		GrpcParams  grpcPrms = null;
		SciRIO.RetMsg grpcResp = null;
		String PID = request.getPID();
		PIDSLoader pids = null;
		String errMsg = null;
		try {
			if (pids == null) {
				pids = PIDSLoader.getInstance(this.xmlUri, this.MSA_BUFFER_TYPE);
			}
			grpcPrms = GrpcDataUtil.parseGrpcData(request);		
			/* xml로 변경함.
			String svcName  = appProps.getProperty(PID + ".service"); // 서비스명
			String queryId  = appProps.getProperty(PID + ".query");   // SqlID
			String methName = appProps.getProperty(PID + ".method");  // 호출할 메서드명
			*/
			String svcName  = pids.getPIDSteps(PID).getService(); // 서비스명
			String queryId  = pids.getPIDSteps(PID).getQuery();   // SqlID
			String methName = pids.getPIDSteps(PID).getMethod();  // 호출할 메서드명
			
			logger.info("service ::: " + svcName);
			logger.info("queryId ::: " + queryId);
			logger.info("method  ::: " + methName);
			
			Object beanObj = context.getBean(svcName);
			if ("N".equals(queryId)) {
				if (request.getParamsMap() != null && request.getParamsMap().size() > 0) {					
					Class prmTypes[] = { Map.class };
					Method method = beanObj.getClass().getDeclaredMethod(methName, prmTypes);
					grpcResp = (SciRIO.RetMsg) method.invoke(beanObj, new Object[] { request.getParamsMap() });
					
				} else {
					Class prmTypes[] = { GrpcParams.class };
					Method method = beanObj.getClass().getDeclaredMethod(methName, prmTypes);
					grpcResp = (SciRIO.RetMsg) method.invoke(beanObj, new Object[] { grpcPrms });
				}
			} else {
				if (request.getParamsMap() != null && request.getParamsMap().size() > 0) {					
					Class prmTypes[] = { String.class, Map.class };
					Method method = beanObj.getClass().getDeclaredMethod(methName, prmTypes);
					grpcResp = (SciRIO.RetMsg) method.invoke(beanObj, new Object[] { queryId, request.getParamsMap() });
					
				} else {
					Class prmTypes[] = { String.class, GrpcParams.class };
					Method method = beanObj.getClass().getDeclaredMethod(methName, prmTypes);
					grpcResp = (SciRIO.RetMsg) method.invoke(beanObj, new Object[] { queryId, grpcPrms });
				}
			}
		} catch (Exception e) {
			logger.error("callRMsg() TRY CATCH");
			errMsg = getPrintStackTrace(e);
			logger.error("126 "+ errMsg);
			grpcResp = null;				
//			logger.error("errMsg.indexOf(\"@\")====="+ errMsg.indexOf("@"));
//			logger.error("errMsg.indexOf(\"###\")====="+ errMsg.indexOf("###"));<eval>
			if (errMsg.indexOf("@") >= 0) {
				errMsg = errMsg.split("\\@")[2];
				results = GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, errMsg, null);
			} else if (errMsg.indexOf("###") >= 0) {
				errMsg = errMsg.split("\\Q###\\E")[1].replaceAll(System.getProperty("line.separator"), "");
				results = GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, errMsg, null);
			} else if (errMsg.indexOf("<eval>") >= 0) {
				errMsg = errMsg.split("\\Q<eval>\\E")[1].replaceAll(System.getProperty("line.separator"), "");
				results = GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, errMsg, null);
			} else if (errMsg.indexOf("Caused by:") >= 0) {
				logger.info("140 :::::::::::::::::"+ errMsg);
				errMsg = errMsg.split("\\QCaused by:\\E")[1];
				errMsg = errMsg.split("\\Qat \\E")[0].replaceAll(System.getProperty("line.separator"), "");
				if (errMsg.indexOf("$#") >= 0) {
					errMsg = errMsg.split("\\Q$#\\E")[1].replaceAll(System.getProperty("line.separator"), "");
				} else if (errMsg.indexOf("BuilderException:") >= 0) {
					errMsg = errMsg.split("\\QBuilderException:\\E")[1].replaceAll(System.getProperty("line.separator"), "");
				}
				results = GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, errMsg, null);	
			} else if (errMsg.indexOf("BuilderException:") >= 0) {
				errMsg = errMsg.split("\\QBuilderException:\\E")[1].replaceAll(System.getProperty("line.separator"), "");
				results = GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, errMsg, null);
			} else if (errMsg.indexOf("$#") >= 0) {
				errMsg = errMsg.split("\\Q$#\\E")[1].replaceAll(System.getProperty("line.separator"), "");
				results = GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, errMsg, null);
			} else if (errMsg.indexOf("at ") >= 0) {
				errMsg = errMsg.split("\\Qat \\E")[1].replaceAll(System.getProperty("line.separator"), "");
				results = GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, errMsg, null);
			} else {
				if (errMsg.length() > 200) {
					errMsg = errMsg.substring(0, 200);
				}
				results = GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, errMsg, null);
			}
			logger.error("results::::::"+ results);
		} finally {		
			try {
				if (errMsg != null) {
					grpcResp = SciRIO.RetMsg.newBuilder()
							.setResults(results)
							.setErrCode(ErrConstance.ERR_9999)
							.setErrMsg(errMsg)
							.build();
				}
				responseObserver.onNext(grpcResp);
				responseObserver.onCompleted();			
			} catch (Exception e1) {
				logger.error("onCompleted() TRY CATCH");
				e1.printStackTrace();
			}			
			if (grpcPrms != null) { try { grpcPrms = null; } catch (Exception e1) {} }
			if (grpcResp != null) { try { grpcResp = null; } catch (Exception e1) {} }
		}	
		logger.info("CALL "+ PID +" METHOD ###################################### END");
	}
	
	public String getPrintStackTrace(Exception e) {        
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));         
        return errors.toString();         
    }
	
	/**
	 * 서비스별 Master 데이터 싱크 서비스
	 * 
	 */
	@Override
	public void getMstInfo(ReqMsg request, StreamObserver<RetMsg> responseObserver) {
		SciRIO.RetMsg retMsg = null;
		String jsonData = null;		
		GrpcResp grpcResp = new GrpcResp();
		Map<String,Object> params = new HashMap<String, Object>();
		try {
			logger.info("agentID ::: "+ request.getAgentID());
			logger.info("tblName ::: "+ request.getMsg());
			String[] vals = null;
			if (request.getMsg().indexOf("|") >= 0) {
				vals = request.getMsg().split("\\|");
			}			
			params.put("agentID", request.getAgentID());
			params.put("tblName", vals[0]); // 예) "tbl_custInfo" 테이블명으로 요청해야 함.
			params.put("svcKey",  vals[1]); // 
			params.put("syncType",  vals[2]); // 
			params.put("chkTime", Integer.parseInt((MST_CHKTIME==null?"5":MST_CHKTIME)));// 예) 5분전까지 데이터 조회
			params.put("SQLMODE", this.SQLMODE); 
			if (!params.containsKey("clang")) {
				params.put("clang", this.CLANG);
			}

			grpcResp = commService.getMstInfo(params);
			
			retMsg = SciRIO.RetMsg.newBuilder()
					.setResults(grpcResp.getResults())
					.setErrCode(grpcResp.getErrCode())
					.setErrMsg(grpcResp.getErrMsg())
					.build();
			
		} catch (Exception e1) {
			logger.error("getMstInfo.onCompleted() TRY CATCH");
			e1.printStackTrace();
			String results = GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, e1.getMessage(), null);
			retMsg = SciRIO.RetMsg.newBuilder()
					.setResults(results)
					.setErrCode(ErrConstance.ERR_9999)
					.setErrMsg(e1.getMessage())
					.build();
		} finally {			
			try {
				responseObserver.onNext(retMsg);
				responseObserver.onCompleted();			
			} catch (Exception e1) {
				logger.error("getMstInfo.onCompleted() TRY CATCH");
				e1.printStackTrace();
			}		
			if (params   != null) { try { params   = null; } catch (Exception e1) {} }
			if (grpcResp != null) { try { grpcResp = null; } catch (Exception e1) {} }
		}
	}
}