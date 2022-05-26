package com.sci4s.grpc.svc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sci4s.cnf.CaseDo;
import com.sci4s.cnf.PIDSLoader;
import com.sci4s.grpc.ErrConstance;
import com.sci4s.grpc.MsaApiGrpc;
import com.sci4s.grpc.SciRIO;
import com.sci4s.grpc.SciRIO.RetMsg;
import com.sci4s.grpc.dao.IDataDao;
import com.sci4s.grpc.dto.GrpcParams;
import com.sci4s.grpc.dto.GrpcResp;
import com.sci4s.grpc.utils.GrpcDataUtil;
import com.sci4s.msa.mapper.CommMapper;
import com.sci4s.utils.AES256Util;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class CommService implements CommSvc {

	Logger logger = LoggerFactory.getLogger(CommService.class);	
	
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
	
	@Value("${mst.thr.uri}")
	String THR_URI;
	
	@Value("${pid.xml.mode}")
	String PIDS_MODE;
	
	@Value("${buffer.type}")
	String BUFFER_TYPE;
	
	@Value("${msa.pids.buffer}")
	String MSA_BUFFER_TYPE;
	
	String xmlUri;
	
	private PIDSLoader pids;
	private IDataDao dataDao;
	private CommMapper commMapper;
	
	@Autowired
	public CommService(CommMapper commMapper
			, IDataDao dataDao) {
		this.commMapper = commMapper;
		this.dataDao = dataDao;
	}
	
	@PostConstruct
    private void init() {
		this.xmlUri = TSYS_URI +"|"+ MSA_AGENTID +"|"+ MSA_ID;	    
		if (pids == null) {
	    	if ("XML".equals(this.PIDS_MODE)) {
	    		try { pids = PIDSLoader.getInstance(null, null); } catch(Exception ex) {}
	    	} else {
	    		try { pids = PIDSLoader.getInstance(this.xmlUri, this.MSA_BUFFER_TYPE); } catch(Exception ex) {}
	    	}
		}
    }
	
    private Method getMapperMethod(String sqlID, String flag) throws Exception {
    	Class<?> klass = CommMapper.class;			
    	try {
	    	if ("LIST".equals(flag)) {
	    		return klass.getMethod(sqlID, new Class[] {List.class});
	    	} else {
	    		return klass.getMethod(sqlID, new Class[] {Map.class});
	    	}
    	} catch(Exception ex) {
			throw ex;
        }
    }	
    
	
    public GrpcResp query4Data(String sqlID, Map<String,String> grpcMap) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		GrpcResp grpcResp = new GrpcResp();
		paramMap.putAll(grpcMap);
		paramMap.put("SQLMODE", this.SQLMODE);
		if (!paramMap.containsKey("clang")) {
			paramMap.put("clang", this.CLANG);
		}

		grpcResp = new DataService().query4Data(BUFFER_TYPE, dataDao, sqlID, paramMap);

		return grpcResp;
	}
	
    /**
	 * 데이터 조회용 공통 서비스 메서드
	 * 
	 * @param  String sqlID
	 * @param  GrpcParams grpcPrms
	 * @return SciRIO.Data
	 */
	public GrpcResp query4Data(String sqlID, GrpcParams grpcPrms) throws Exception {
		Map<String, Object> paramMap = GrpcDataUtil.getParams4Map("params", grpcPrms.getData());
		Map<String, Object> commInfoMap = this.getCommInfoMap(grpcPrms);
		GrpcResp grpcResp = new GrpcResp();
		paramMap.putAll(commInfoMap);
		
		if (!paramMap.containsKey("clang")) {
			paramMap.put("clang", this.CLANG);
		}

		grpcResp = new DataService().query4Data(BUFFER_TYPE, dataDao, sqlID, paramMap);
		
		return grpcResp;
	}
	
	/**
	 * pid-resolver.xml에서 데이터 처리용으로 사용됨.
	 * 예) 견적작성 화면으로 이동 시에 견적확인일시를 업데이트함.
	 * 
	 * @param sqlID
	 * @param grpcMap
	 * @return
	 * @throws Exception
	 */
	public GrpcResp query4Update(String sqlID, Map<String,String> grpcMap) throws Exception {	
		GrpcResp grpcResp = new GrpcResp();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.putAll(grpcMap);
		paramMap.put("SQLMODE", this.SQLMODE);
		if (!paramMap.containsKey("clang")) {
			paramMap.put("clang", this.CLANG);
		}
			
		grpcResp = new DataService().query4Update(BUFFER_TYPE, dataDao, sqlID, paramMap);
		
		return grpcResp;
	}
	
	@Transactional(rollbackFor = {Exception.class})
	public GrpcResp query4Update(String sqlID, GrpcParams grpcPrms) throws Exception {
		return query4Execute("query4Update", sqlID, grpcPrms);
	}
		
	@Transactional(rollbackFor = {Exception.class})
	public GrpcResp query4Update1(String sqlID, GrpcParams grpcPrms) throws Exception {	
		return query4Execute("query4Data", sqlID, grpcPrms);
	}
	
	private Map<String,Object> getCommInfoMap(GrpcParams grpcPrms) throws Exception {
		AES256Util aes256 = null;
		String[] arrCsKey = null;
		
		Map<String,Object> commInfoMap = new HashMap<String, Object>();
		try {
			commInfoMap = new DataService().getCommInfoMap(grpcPrms);
			commInfoMap.put("SQLMODE",  this.SQLMODE);
			commInfoMap.put("clang",    (grpcPrms.getClang()==null?this.CLANG:grpcPrms.getClang()));
			commInfoMap.put("TSYS_URI", this.TSYS_URI);
			commInfoMap.put("THR_URI", this.THR_URI);
			commInfoMap.put("BUFFERTYPE",  this.BUFFER_TYPE);
			return commInfoMap;	
		} catch(Exception ex) {
			throw new Exception("@FAIL@Not have access rights.@FAIL@");
		}  finally {
        	if (commInfoMap != null) {
        		try { commInfoMap = null; } catch(Exception ex) { }
        	}
        }
	}
	
	@Transactional
	private GrpcResp query4Execute(String flag, String sqlID, GrpcParams grpcPrms) throws Exception {
		if (pids == null) {
			if ("XML".equals(this.PIDS_MODE)) {
	    		try { pids = PIDSLoader.getInstance(null, null); } catch(Exception ex) {}
	    	} else {
	    		try { pids = PIDSLoader.getInstance(this.xmlUri, this.MSA_BUFFER_TYPE); } catch(Exception ex) {}
	    	}
		}
		GrpcResp grpcResp = new GrpcResp();
		String PID = (grpcPrms.getpID()==null?"":grpcPrms.getpID());
		Map<String, Object> paramMap = null;
		Map<String,Object> commInfoMap = this.getCommInfoMap(grpcPrms);
		
		paramMap = GrpcDataUtil.getParams4Map("params", grpcPrms.getData());	
		paramMap.putAll(commInfoMap);
		logger.info("367 query4Execute.grpcPrms."+ flag +".paramMap():::"+ paramMap);
		
		grpcResp = new DataService().query4Execute(BUFFER_TYPE, dataDao, pids, PID, flag, sqlID, paramMap, commInfoMap);
		
		return grpcResp;
	}
	
	@Transactional(rollbackFor = {Exception.class})
	public GrpcResp query4Updates(String sqlID, GrpcParams grpcPrms) throws Exception {	
		String PID     = grpcPrms.getpID();
		GrpcResp grpcResp = new GrpcResp();
		Map<String,Object> commInfoMap = this.getCommInfoMap(grpcPrms);
		Map<String, Object> paramMap = null;
		paramMap = GrpcDataUtil.getParams("params", grpcPrms.getData(), true);
		
		grpcResp = new DataService().query4Updates(BUFFER_TYPE, dataDao, pids, sqlID, PID, paramMap, commInfoMap);

		
		return grpcResp;
	}
	
	/**
	 * 리스트로 넘어오는 데이터를 생성/수정/삭제 처리하는 서비스 메서드
	 * 
	 * @param  String sqlID
	 * @param  GrpcParams grpcPrms
	 * @return SciRIO.RetMsg
	 */
	@Transactional
	public GrpcResp query4Selects(String sqlID, GrpcParams grpcPrms) throws Exception {
		return query4Updates(sqlID, grpcPrms);
	}
	
	/**
	 * Master 테이블 정보가 변경되었을 경우, 싱크하기 위한 서비스
	 * 
	 * @param  Map<String,Object> paramMap
	 * @return String results
	 */
	public GrpcResp getMstInfo(GrpcParams grpcPrms) throws Exception {
		Map<String, Object> paramMap = null;
		paramMap = GrpcDataUtil.getParams4Map("params", grpcPrms.getData());
//		paramMap.put("chkTime",  this.MST_CHKTIME);
		paramMap.put("SQLMODE",  this.SQLMODE);
//		logger.info("paramMap :::: " + paramMap);
		return new DataService().getMstInfo(dataDao, paramMap);
	}
	
	public GrpcResp getMstInfo(Map<String, Object> paramMap) throws Exception {
//		paramMap.put("chkTime",  this.MST_CHKTIME);
		paramMap.put("SQLMODE",  this.SQLMODE);
		
		return new DataService().getMstInfo(dataDao, paramMap);
	}
}