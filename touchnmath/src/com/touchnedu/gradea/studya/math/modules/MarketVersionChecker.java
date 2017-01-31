package com.touchnedu.gradea.studya.math.modules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MarketVersionChecker {
	private static final String uri = 
															"https://play.google.com/store/apps/details?id=";
	private static final String marketUri = "market://details?id=";
	
	public static void getMarketVersion(final String packageName) {
		new Thread() {
			@Override
			public void run() {
				try {
					/**
					 * @param select : class='content'의 엘리먼트를 가지고 온다.
					 */
					Document doc = Jsoup.connect(uri + packageName).get();
					Elements Version = doc.select(".content");
					
					for(Element mElement : Version) {
						if(mElement.attr("itemprop").equals("softwareVersion")) {
							DataManager.storeVersion = mElement.text().trim();
						}
					}
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}.start();
	}
	
	public static String marketAddress(final String packageName) {
		return marketUri + packageName;
	}
	
	public static String getMarketVersionFast(String packageName) {
		String mData = "", mVer = null;
		try {
			URL mUrl = new URL(uri + packageName);
			HttpURLConnection mConnection = (HttpURLConnection)mUrl.openConnection();
			if(mConnection == null) {
				return null;
			}
			
			mConnection.setConnectTimeout(5000);
			mConnection.setUseCaches(false);
			mConnection.setDoOutput(true);
			
			if(mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader mReader = new BufferedReader(
													new InputStreamReader(mConnection.getInputStream()));
				
				while(true) {
					String line = mReader.readLine();
					if(line == null)
						break;
					mData += line;
				}
				mReader.close();
				
			}
			mConnection.disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		String startToken = "softwareVersion\">";
		String endToken = "<";
		int index = mData.indexOf(startToken);
		
		if(index == -1) {
			mVer = null;
		} else {
			mVer = mData.substring(index + startToken.length(), 
														 index + startToken.length() + 100);
			mVer = mVer.substring(0, mVer.indexOf(endToken)).trim();
		}
		
		return mVer;
	}
	
}
