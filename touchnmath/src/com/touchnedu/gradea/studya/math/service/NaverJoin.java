package com.touchnedu.gradea.studya.math.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.util.ErrorEventSender;

import android.content.Context;
import android.util.Log;

public class NaverJoin {
	private final String uri = "http://touchnbox.cafe24.com/exist.jsp?id=";
	Context mContext;
	
	public NaverJoin(Context mContext) {
		if(this.mContext == null)
			this.mContext = mContext;
	}
	
	public boolean isExist(String id) {
		StringBuffer str = new StringBuffer();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri + id);
						
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			
			int statusCode = statusLine.getStatusCode();
			
			if(statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				Scanner sc = new Scanner(is);
				while(sc.hasNext()) 
					str.append(sc.nextLine());
				sc.close();
			} else {
				Log.d("prjt", "ERROR!");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(Integer.parseInt(str.toString()) > 0) {
			DataManager.curMemNo = str.toString();
			return true;
		}	else { 
			return false;
		}
	}
	
	public String joinNaverId() {
		String result = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 5000);
			
			ArrayList<NameValuePair> arr = new ArrayList<NameValuePair>();
			arr.add(new BasicNameValuePair("id", DataManager.curMemEmail));
			arr.add(new BasicNameValuePair("password", ""));
			arr.add(new BasicNameValuePair("name", DataManager.curMemName));
			arr.add(new BasicNameValuePair("nickname", DataManager.curMemNickname));
			arr.add(new BasicNameValuePair("photo", DataManager.curMemPhoto));
			arr.add(new BasicNameValuePair("sex", DataManager.curMemSex));
			arr.add(new BasicNameValuePair("age", DataManager.curMemAge));
			arr.add(new BasicNameValuePair("job", DataManager.curJob));
			arr.add(new BasicNameValuePair("device_id", ""));
//			arr.add(new BasicNameValuePair("device_id", 
//																					DeviceInfo.getDevicesUUID(mContext)));
			
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(arr, HTTP.UTF_8);
			
			String uri = "http://touchnbox.cafe24.com/insert.jsp";
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(ent);
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			
			if(entity != null) {
				result = EntityUtils.toString(entity);
				
			}
			
		} catch (Exception e) {
			ErrorEventSender.sendErrorMsg("- NaverJoin Err\n" + e.getMessage());
			result = "error";
		} 
		result = result.trim();
		
		return result;
	}
	
}
