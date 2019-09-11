package com.shineyue.certSign.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

/**
 * 河北省神玥软件科技有限公司 版权所有
 * 
 * Http工具类
 * @author Administrator
 */
public class HttpService {
	private static final Logger log = LoggerFactory.getLogger(HttpService.class);
	private static PoolingHttpClientConnectionManager cm = null;
	private static int statusCode = 200;
	

	
	public static String doGet(String url) throws Exception {
		String strResult = "";
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpGet get = new HttpGet(url);
			httpResponse = httpClient.execute(get);
			if (httpResponse.getStatusLine().getStatusCode() == statusCode) {
				HttpEntity entity = httpResponse.getEntity();
				if (null != entity){
					strResult = EntityUtils.toString(entity);
				}
					
			} else {
				strResult = "没返回正确代码,代码为：" + httpResponse.getStatusLine().getStatusCode() + "";
				log.info("《《《《《《《《" + strResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpResponse != null){
				httpResponse.close();
			}
		}
		return strResult;
	}
	public static String doPost(String url, String json) throws Exception {
		String strResult = "";
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpPost post = new HttpPost(url);

			StringEntity uefEntity = new StringEntity(json.toString(), "UTF-8");
			uefEntity.setContentType("application/json");
			post.setEntity(uefEntity);
			JsonObject json2 = new JsonObject();
			json2.addProperty("blqd", "gt");
			post.setHeader("usr",json2.toString());
			httpResponse = httpClient.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == statusCode) {
				HttpEntity entity = httpResponse.getEntity();
				if (null != entity){
					strResult = EntityUtils.toString(entity);
					log.info("返回内容:{}",strResult);
				}
			} else {
				strResult = "没返回正确代码,代码为：" + httpResponse.getStatusLine().getStatusCode() + "";
				log.info("《《《《《《《《" + strResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpResponse != null){
				httpResponse.close();
			}
		}
		return strResult;
	}
	public static String doPost(String url, String json, String user) throws Exception {
		String strResult = "";
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpPost post = new HttpPost(url);

			StringEntity uefEntity = new StringEntity(json.toString(), "UTF-8");
			uefEntity.setContentType("application/json");
			post.setEntity(uefEntity);
			JsonParser jsonparser = new JsonParser();
			JsonObject json2 = jsonparser.parse((String) user).getAsJsonObject();
			json2.addProperty("blqd", "gt");
			post.setHeader("usr", json2.toString());

			httpResponse = httpClient.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == statusCode) {
				HttpEntity entity = httpResponse.getEntity();
				if (null != entity){
					strResult = EntityUtils.toString(entity);
				}
					
			} else {
				strResult = "没返回正确代码,代码为：" + httpResponse.getStatusLine().getStatusCode() + "";
				log.info("《《《《《《《《" + strResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpResponse != null){
				httpResponse.close();
			}
				
		}
		return strResult;
	}
	
	public static String doPost4(String url,  Map<String, Object> map) throws Exception {
		String strResult = "";
		//建立httpclient
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		CloseableHttpResponse response = null;

		try {
			HttpPost httpPost = new HttpPost(url);
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for(Map.Entry<String, Object> entry :map.entrySet()){
				 parameters.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));  
			}
			System.out.println("---------------parameters------"+parameters.toString());
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
			httpPost.setEntity(formEntity);			
			JsonObject json2 = new JsonObject();
			json2.addProperty("blqd", "gt");
			 // 发出请求    
			httpPost.setHeader("usr",json2.toString());
			//执行get请求，并结果保存
			response = httpClient.execute(httpPost);
	
			int code = response.getStatusLine().getStatusCode();
			if (code == statusCode) {
				//将保存的response转为实体
				HttpEntity entity = response.getEntity(); 
				if (entity != null){
					strResult = EntityUtils.toString(entity);
				}
				log.info("《《《《《result《《《" + strResult);
			} else {
				httpPost.abort();
				strResult = "没返回正确代码,代码为：" + response.getStatusLine().getStatusCode() + "";
				log.info("《《《《《《《《" + strResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null){
				response.close();
			}
				
		}
		return strResult;
	}
	
	/**
	 * 流程发起专用
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception
	 * @author ntj
	 */
	public static String doPostMap(String url, Map<String, Object> map, JSONObject params) throws Exception {
		String strResult = "";
		//建立httpclient
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		CloseableHttpResponse response = null;

		try {
			HttpPost httpPost = new HttpPost(url);
			JsonObject headerParams= new JsonObject(); 
			 
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			 
			for(Map.Entry<String, Object> entry :map.entrySet()){
				parameters.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));   
			}
			 
			System.out.println("---------------parameters------"+parameters.toString());
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
			httpPost.setEntity(formEntity);			
			 // 发出请求    
			headerParams.addProperty("userId", params.getString("userid"));
			headerParams.addProperty("blqd", params.getString("blqd"));
			httpPost.setHeader("usr",headerParams.toString());
			//执行get请求，并结果保存
			response = httpClient.execute(httpPost);
	
			int code = response.getStatusLine().getStatusCode();
			if (code == statusCode) {
				//将保存的response转为实体
				HttpEntity entity = response.getEntity(); 
				if (entity != null){
					strResult = EntityUtils.toString(entity);
				}
				log.info("《《《《《result《《《" + strResult);
			} else {
				httpPost.abort();
				strResult = "没返回正确代码,代码为：" + response.getStatusLine().getStatusCode() + "";
				log.info("《《《《《《《《" + strResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null){
				response.close();
			}
				
		}
		return strResult;
	}

}
