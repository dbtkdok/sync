package com.sci4s.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

public class XMLUtils {
	
	/**
	 * 문자열 값을 특정 클래스 형으로 변환하여 Object로 리턴하는 메서드
	 * 
	 * @param  String  val
	 * @param  Class   param
	 * @return Object
	 * @throws Exception
	 */
	public static String getRoleMapXml(List<Map<String, Object>> val, String roleNM, String pathNM) throws Exception {
		String retVal = null;
		BufferedReader write = null;
		try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
            Document doc = docBuilder.newDocument();
            doc.setXmlStandalone(true); //standalone="no" 를 없애준다.
            ProcessingInstruction xmlstylesheet =  doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"topMenu.xsl\"");
            
            Element roles = doc.createElement("roles");
            doc.appendChild(roles);
            
            for(int ii=0; ii<val.size(); ii++) {
            	Map<String, Object> paramMap = val.get(ii);
            	Element row = doc.createElement("row");
            	roles.appendChild(row);
            	
            	Iterator<String> keys = paramMap.keySet().iterator();
            	while ( keys.hasNext() ) {
            		String key = keys.next();
            		Element name = doc.createElement("" + key);
            		
            		name.appendChild(doc.createTextNode("" + paramMap.get(key)));
                	
            		row.appendChild(name);
            	}
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //정렬 스페이스4칸
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //들여쓰기
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes"); //doc.setXmlStandalone(true); 했을때 붙어서 출력되는부분 개행
            
            File Folder = new File(pathNM);
            
            if (!Folder.exists()) {
        		try{
        		    Folder.mkdir();
        		} catch(Exception e){
        		    e.getStackTrace();
        		}
            }
            
            StreamResult result = new StreamResult(new FileOutputStream(new File(pathNM + "/" +roleNM + ".xml")));
            DOMSource source = new DOMSource(doc); 	
            transformer.transform(source, result);
            
        }catch (Exception e){
            e.printStackTrace();
        }
		
		return retVal;
	}
	
	public static String saveLeftMenuXml(List<Map<String, Object>> val, String roleNM, String xmlGB, String MENU_PATH) throws Exception {
		String retVal = null;
		String filePath = null;
		try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
            Document doc = docBuilder.newDocument();
            doc.setXmlStandalone(true); //standalone="no" 를 없애준다.
            ProcessingInstruction xmlstylesheet =  doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"topMenu.xsl\"");
            
            Element menus = doc.createElement("menus");
            doc.appendChild(menus);
            String menuKey = "";
            Element menuLIST = null;
            
            if(val != null && val.size() > 0) {
	            for(int ii=0; ii<val.size(); ii++) {
	            	Map<String, Object> paramMap = val.get(ii);
	            	if(menuKey.equals(paramMap.get("parMenuID"))) {
	            		
	            	} else {
	            		Element menu = doc.createElement("menu");
	                	menus.appendChild(menu);
	                	
	                	Element topMenu = doc.createElement("topMenuID");
	                	topMenu.appendChild(doc.createTextNode("" + paramMap.get("topMenuID")));
	                	
	                	Element parMenuID = doc.createElement("parMenuID");
	                	parMenuID.appendChild(doc.createTextNode("" + paramMap.get("parMenuID")));
	                	
	                	Element parMenuNM = doc.createElement("parMenuNM");
	                	parMenuNM.appendChild(doc.createTextNode("" + paramMap.get("parMenuNM")));
	                	
	                	Element parLangEN = doc.createElement("parLangEN");
	                	parLangEN.appendChild(doc.createTextNode("" + paramMap.get("parLangEN")));
	                	
	                	Element parLangCN = doc.createElement("parLangCN");
	                	parLangCN.appendChild(doc.createTextNode("" + paramMap.get("parLangCN")));
	                	
	                	Element parLang = doc.createElement("parLang");
	                	parLang.appendChild(doc.createTextNode("" + paramMap.get("parLang")));
	                	
	                	menuLIST = doc.createElement("menuLIST");
	                	
	                	menu.appendChild(topMenu);
	                	menu.appendChild(parMenuID);
	                	menu.appendChild(parMenuNM);
	                	menu.appendChild(parLangEN);
	                	menu.appendChild(parLangCN);
	                	menu.appendChild(parLang);
	                	menu.appendChild(menuLIST);
	            	}
	            	
	            	Element xMenu = doc.createElement("xMenu");
	            	menuLIST.appendChild(xMenu);
	            	
	            	Iterator<String> keys = paramMap.keySet().iterator();
	            	while ( keys.hasNext() ) {
	            		String key = keys.next();
	            		if(!key.equals("topMenuID") && !key.equals("parMenuID") && !key.equals("parMenuNM") && !key.equals("parLangEN") && !key.equals("parLangCN") && !key.equals("parLang")) {
	            			Element name = doc.createElement("" + key);
	                		if(key.equals("linkUri")) {
	                			name.appendChild(doc.createCDATASection("" + paramMap.get(key)));
	                		} else {
	                			name.appendChild(doc.createTextNode("" + paramMap.get(key)));
	                		}
	                		xMenu.appendChild(name);
	            		}
	            	}
	            	menuKey = (String) paramMap.get("parMenuID");
	            }
            }
            
            Element root = doc.getDocumentElement();
            doc.insertBefore(xmlstylesheet, root);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //정렬 스페이스4칸
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //들여쓰기
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes"); //doc.setXmlStandalone(true); 했을때 붙어서 출력되는부분 개행
            
            File Folder = new File(MENU_PATH);
            
            if (!Folder.exists()) {
        		try{
        		    Folder.mkdir();
        		} catch(Exception e){
        		    e.getStackTrace();
        		}
            }
            
            filePath = MENU_PATH + "/left";
            
            File file = new File(filePath);
            if(!file.exists()) {
            	file.mkdir();
            }
            
            StreamResult result = new StreamResult(new FileOutputStream(new File(filePath + "/" + roleNM + ".xml")));
            
            DOMSource source = new DOMSource(doc); 	
            
            transformer.transform(source, result); 
           
            retVal = "SUCCEES";
            
        }catch (Exception e){
            e.printStackTrace();
        }
		return retVal;
	}
    
    public static String saveTopMenuXml(List<Map<String, Object>> val, String roleNM, String xmlGB, String MENU_PATH) throws Exception {
		String retVal = null;
		String filePath = null;
		try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
            Document doc = docBuilder.newDocument();
            doc.setXmlStandalone(true); //standalone="no" 를 없애준다.
            ProcessingInstruction xmlstylesheet =  doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"topMenu.xsl\"");
            
            Element menus = doc.createElement("menus");
            doc.appendChild(menus);
            
            if(val != null && val.size() > 0) {
	            for(int ii=0; ii<val.size(); ii++) {
	            	Map<String, Object> paramMap = val.get(ii);
	            	Element menu = doc.createElement("menu");
	            	menus.appendChild(menu);
	            	Iterator<String> keys = paramMap.keySet().iterator();
	            	while ( keys.hasNext() ) {
	            		String key = keys.next();
	            		Element name = doc.createElement("" + key);
	            		if(key.equals("linkUri")) {
	            			name.appendChild(doc.createCDATASection("" + paramMap.get(key)));
	            		} else {
	            			name.appendChild(doc.createTextNode("" + paramMap.get(key)));
	            		}
	                	
	                    menu.appendChild(name);
	            	}
	            }
            }
            
            Element root = doc.getDocumentElement();
            doc.insertBefore(xmlstylesheet, root);
            
            // XML 파일로 쓰기
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
 
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //정렬 스페이스4칸
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //들여쓰기
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes"); //doc.setXmlStandalone(true); 했을때 붙어서 출력되는부분 개행
            
            File Folder = new File(MENU_PATH);
            
            if (!Folder.exists()) {
        		try{
        		    Folder.mkdir();
        		} catch(Exception e){
        		    e.getStackTrace();
        		}
            }
            
            filePath = MENU_PATH + "/top";
            
            File file = new File(filePath);
            if(!file.exists()) {
            	file.mkdirs();
            }
            
            StreamResult result = new StreamResult(new FileOutputStream(new File(filePath + "/" + roleNM + ".xml")));
            DOMSource source = new DOMSource(doc); 	
            
            transformer.transform(source, result); 
            
            retVal = "SUCCEES";
        } catch (Exception e){
            e.printStackTrace();
        }
		return retVal;
	}
    
    public static String saveDictionaryXml(List<Map<String, Object>> val, String keyNM, String local, String xmlGB, String MENU_PATH) throws Exception {
		String retVal = null;
		String filePath = null;
//		logger.info("local ::: " + local + "  xmlGB ::: " + xmlGB + "  keyNM ::: " + keyNM);
		try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
            Document doc = docBuilder.newDocument();
            doc.setXmlStandalone(true); //standalone="no" 를 없애준다.
            
            Element menus = doc.createElement("properties");
            doc.appendChild(menus);
            
            if(val != null && val.size() > 0) {
	            for(int ii=0; ii<val.size(); ii++) {
	            	Map<String, Object> paramMap = val.get(ii);
	            	Element menu = doc.createElement("entry");
	            	menu.setAttribute("key", (String) paramMap.get("dictID"));
	            	menu.appendChild(doc.createCDATASection("" + paramMap.get(keyNM)));
	            	menus.appendChild(menu);
	            }
            }
            
            // XML 파일로 쓰기
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
 
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //정렬 스페이스4칸
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //들여쓰기
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes"); //doc.setXmlStandalone(true); 했을때 붙어서 출력되는부분 개행
            DOMImplementation domImpl = doc.getImplementation();
            DocumentType doctype = domImpl.createDocumentType("doctype",
            	    "properties SYSTEM", "http://java.sun.com/dtd/properties.dtd");
        	transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
        	transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
            
            filePath = MENU_PATH  + "/dictionary";
            
            File file = new File(filePath);
            if(!file.exists()) {
            	file.mkdirs();
            }
            
            StreamResult result = new StreamResult(new FileOutputStream(new File(filePath + "/message_" + local + ".xml")));
            DOMSource source = new DOMSource(doc); 	
            
            transformer.transform(source, result); 
//            logger.info("result :: " + result.toString());
            retVal = "SUCCEES";
        } catch (Exception e){
            e.printStackTrace();
        }
		return retVal;
	}
}
