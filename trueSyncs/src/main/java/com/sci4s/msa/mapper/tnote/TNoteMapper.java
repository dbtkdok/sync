package com.sci4s.msa.mapper.tnote;

import java.util.Map;

public interface TNoteMapper {
	public int existsTblAttach(Map<String,Object> param) throws Exception;
	public int insTblAttach(Map<String,Object> param) throws Exception;
	public int updTblAttach(Map<String,Object> param) throws Exception;
}
