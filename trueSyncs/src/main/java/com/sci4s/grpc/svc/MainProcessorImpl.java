package com.sci4s.grpc.svc;

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
import com.sci4s.fbs.ReqMsg;
import com.sci4s.fbs.RetMsg;
import com.sci4s.grpc.ErrConstance;
import com.sci4s.grpc.SciRIO;
import com.sci4s.grpc.dto.GrpcParams;
import com.sci4s.grpc.dto.GrpcResp;
import com.sci4s.grpc.utils.GrpcDataUtil;

import io.grpc.stub.StreamObserver;

@Service
public class MainProcessorImpl implements MainProcessor {

	Logger logger = LoggerFactory.getLogger(MainProcessorImpl.class);
	
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
	private CommService commService;
	
	@Autowired
	public  MainProcessorImpl(ApplicationContext context
			, CommService commService){
	    this.context = context;
	    this.commService = commService;
	}
	
	@PostConstruct
    private void init() {
		this.xmlUri = TSYS_URI +"|"+ MSA_AGENTID +"|"+ MSA_ID;	    
	    if (pids == null) {
			try { pids = PIDSLoader.getInstance(this.xmlUri, this.MSA_BUFFER_TYPE); } catch(Exception ex) {}
		}
    }
	
	private String getPrintStackTrace(Exception e) {        
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));         
        return errors.toString();         
    }
	
	@Override
	public GrpcResp callRMsg(GrpcParams grpcPrms) {
//		logger.info("CALL "+ request.getPID() +" METHOD ###################################### START");
		String results = "";
		String errMsg = null;
		GrpcResp grpcResp = new GrpcResp();
		
		String PID = grpcPrms.getpID();
		PIDSLoader pids = null;
		try {
			if (pids == null) {
				try { pids = PIDSLoader.getInstance(this.xmlUri, this.MSA_BUFFER_TYPE); } catch(Exception ex) {}
			}
			String svcName  = pids.getPIDSteps(PID).getService(); // 서비스명
			String queryId  = pids.getPIDSteps(PID).getQuery();   // SqlID
			String methName = pids.getPIDSteps(PID).getMethod();  // 호출할 메서드명
			
			logger.info("service ::: " + svcName);
			logger.info("queryId ::: " + queryId);
			logger.info("method  ::: " + methName);
			
			Object beanObj = context.getBean(svcName);
			if ("N".equals(queryId)) {
				Class prmTypes[] = { GrpcParams.class };
				Method method = beanObj.getClass().getDeclaredMethod(methName, prmTypes);
				grpcResp = (GrpcResp) method.invoke(beanObj, new Object[] { grpcPrms });
			} else {
				Class prmTypes[] = { String.class, GrpcParams.class };
				Method method = beanObj.getClass().getDeclaredMethod(methName, prmTypes);
				grpcResp = (GrpcResp) method.invoke(beanObj, new Object[] { queryId, grpcPrms });
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
			grpcResp.setErrCode(ErrConstance.ERR_9999);
			grpcResp.setErrMsg(errMsg);
			grpcResp.setResults(results);
			
			logger.error("results::::::"+ results);
		} finally {		
			if (grpcPrms != null) { try { grpcPrms = null; } catch (Exception e1) {} }
//			if (grpcResp != null) { try { grpcResp = null; } catch (Exception e1) {} }
//			if (retMsg != null) { try { retMsg = null; } catch (Exception e1) {} }
		}	
		logger.info("CALL "+ PID +" METHOD ###################################### END");
		return grpcResp;
	}

	@Override
	public GrpcResp getMstInfo(Map<String, Object> params) {
		String jsonData = null;	
		String errCode = "0";	
		String errMsg = "";	
		GrpcResp grpcResp = new GrpcResp();
		
		params.put("chkTime", Integer.parseInt((MST_CHKTIME==null?"5":MST_CHKTIME)));// 예) 5분전까지 데이터 조회
		params.put("SQLMODE", this.SQLMODE); 
		
		if (!params.containsKey("clang")) {
			params.put("clang", this.CLANG);
		}
		try {
			grpcResp = commService.getMstInfo(params);
		} catch (Exception e) {
			grpcResp.setErrCode(ErrConstance.ERR_9999);
			grpcResp.setErrMsg(e.getMessage());
			grpcResp.setResults(GrpcDataUtil.getGrpcResults(ErrConstance.ERR_9999, e.getMessage(), null));
		}

		return grpcResp;
	}
}
