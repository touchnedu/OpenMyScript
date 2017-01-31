package com.touchnedu.gradea.studya.math.util;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class ErrorEventSender {
	private static final String URL = "http://touchnbox.cafe24.com/error_info.jsp";
	
	public static void sendErrorMsg(String msg) {
		HttpClient httpClient = new DefaultHttpClient();
		try {
			ArrayList<NameValuePair> arrList = new ArrayList<NameValuePair>();
			arrList.add(new BasicNameValuePair("error", msg));
			
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);
			
			HttpPost httpPost = new HttpPost(URL);
			UrlEncodedFormEntity entityRequest = 
																		new UrlEncodedFormEntity(arrList, "UTF-8");
			
			httpPost.setEntity(entityRequest);
			httpClient.execute(httpPost);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
