package com.touchnedu.gradea.studya.math.membership;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.util.ErrorEventSender;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MathUserNetwork {

	static String host = "http://touchnbox.cafe24.com/";

	DefaultHttpClient httpClient = null;
	CookieStore cookies = null;
	HttpResponse response = null;
	HttpPost httpPost = null;

	public final static int OKAY = 0;
	public final static int ERROR = 1;
	public final static int NOT_MATCH = 2;
	public final static int NO_NETWORK = -1;
	public final static int ALREADY = 99;
	private static final String PRJT = "prjt";

	private boolean isLoggedIn = false;

	Context context;

	public MathUserNetwork(Context context) {
		this.context = context;

		BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		HttpProtocolParams.setContentCharset(params, "UTF-8");

		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(params, timeoutSocket);

		httpClient = new DefaultHttpClient(cm, params);
	}

	public static String convertStreamToString(InputStream is) {

		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public boolean checkNetwork(Context context) {
//		Log.d("INTERNET STATE", "INTERNET STATE:CONTEXT");
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
//		Log.d("INTERNET STATE", "INTERNET STATE:GET ACTIVE NETWORK");
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
//			Log.d("INTERNET STATE", "INTERNET STATE:TRY HTTPURL");
			try {
				// HttpURLConnection 객체 생성 -> URL 연결(웹페이지 연결)
				HttpURLConnection urlc = (HttpURLConnection)(new URL("http://www.touchnedu.com").openConnection());
				// Request Header 값 세팅(String key, String value)
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				// 서버 접속시 연결 시간
				urlc.setConnectTimeout(1500);
				urlc.connect();
//				Log.d("INTERNET STATE", "INTERNET STATE:TRY HTTPURL-" + (urlc.getResponseCode() == 200));
				return (urlc.getResponseCode() == 200);
			} catch (Exception e) {
//				Log.d("INTERNET STATE", "INTERNET STATE:FALSE");
				e.printStackTrace();
			}
		}
//		Log.d("INTERNET STATE", "INTERNET STATE:RESULT FALSE");
		return false;
	}

	private HttpEntity getEntity(ArrayList<NameValuePair> values, String operation)
												throws ClientProtocolException, IOException, 
															 SocketTimeoutException, ConnectTimeoutException {

		HttpPost httpPost = new HttpPost(host + operation);
		Log.d("HttpPost", httpPost.getURI().toString());
		httpPost.setEntity(new UrlEncodedFormEntity(values, HTTP.UTF_8));
		HttpResponse response = httpClient.execute(httpPost);
		
		return response.getEntity();
	}

	public boolean checkAutoLogin(Context context) {
		SharedPreferences userDetails = context.getSharedPreferences(
																					"userdetails", Context.MODE_PRIVATE);
		String Uname = userDetails.getString("username", "");
		String pass = userDetails.getString("password", "");

		if (Uname.equals("") && pass.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	// 비밀번호 찾기 (미완료)
	public int findPassword(String email, StringBuilder msg) {
		if (!checkNetwork(context))
			return NO_NETWORK;

		Log.d("HttpPost Email Find", email);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
			nameValuePairs.add(new BasicNameValuePair("user_email", email));
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpEntity resEntity = null;
		try {
			resEntity = getEntity(nameValuePairs, "touch/touchif/member/member_pw_search.php");
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return NO_NETWORK;
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return NO_NETWORK;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (resEntity != null) {
			InputStream instream;
			try {
				instream = resEntity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				try {
					JSONObject json = new JSONObject(result);
					String state = json.get("state").toString();
					msg.append(json.get("msg").toString());

					if (state.equals("OK")) {
						return OKAY;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return MathUserNetwork.ERROR;
	}

	// 비밀번호 변경
	public int userPwdChange(String curPwd, String newPwd, StringBuilder msg) {
		if (!checkNetwork(context))
			return NO_NETWORK;

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
			nameValuePairs.add(new BasicNameValuePair("email", DataManager.curMemEmail));
			nameValuePairs.add(new BasicNameValuePair("curPwd", curPwd));
			nameValuePairs.add(new BasicNameValuePair("newPwd", newPwd));
		} catch (Exception e) {
			e.printStackTrace();
		}

		int result = 0;
		HttpEntity entity = null;
		try {
			entity = getEntity(nameValuePairs, "change_password.jsp");
			
			if (entity != null) {
//				Log.i("prjt", "entity.toString : " + EntityUtils.toString(entity));
				if(EntityUtils.toString(entity).trim().equals("success"))
					result = OKAY;
				else
					result = NOT_MATCH;
			} 
			Log.i("prjt", "result : " + result);
			
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			result = NO_NETWORK;
			
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			result = NO_NETWORK;
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return result;
	}

	// 회원탈퇴
	public int userQuit() {
		StringBuffer str = new StringBuffer();
		int result = 1;
		if (!checkNetwork(context)) {
			return NO_NETWORK;
			
		} else {
			String deleteUrl = "http://touchnbox.cafe24.com/delete.jsp?no=";
			
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(deleteUrl + DataManager.curMemNo);
			HttpEntity entity = null;
			
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if(statusCode == 200) {
					entity = response.getEntity();
					InputStream is = entity.getContent();
					Scanner sc = new Scanner(is);
					while(sc.hasNext())
						str.append(sc.nextLine());
					sc.close();
				} else {
					writeLog("error!");
				}
				
				if(str.toString().equals("success"))
					result = OKAY;
				
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				result = NO_NETWORK;
			} catch (ConnectTimeoutException e) {
				e.printStackTrace();
				result = NO_NETWORK;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	// 아이디 찾기(미완료)
	public int findId(String username, String password, StringBuilder msg) {
		if (!checkNetwork(context))
			return NO_NETWORK;

		Log.d("HttpPost Find Password", password + username);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
			nameValuePairs.add(new BasicNameValuePair("user_name", username));
			nameValuePairs.add(new BasicNameValuePair("user_pwd", password));
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpEntity resEntity = null;
		try {
			resEntity = getEntity(nameValuePairs, "touch/touchif/member/member_id_search.php");
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return NO_NETWORK;
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return NO_NETWORK;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (resEntity != null) {
			InputStream instream;
			try {
				instream = resEntity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				try {
					JSONObject json = new JSONObject(result);
					String state = json.get("state").toString();
					msg.append(json.get("msg").toString());

					if (state.equals("OK")) {
						return OKAY;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return MathUserNetwork.ERROR;
	}

	// 회원가입(추가해야함)
	public int register(String name, String pwd, String email, String birth, 
																											String job, String sex) {
		if (!checkNetwork(context)) {
			ErrorEventSender.sendErrorMsg("NO_NETWORK");
			return NO_NETWORK;
		}
		
		ArrayList<NameValuePair> arr = new ArrayList<NameValuePair>();
		try {
			arr.add(new BasicNameValuePair("id", email));
			arr.add(new BasicNameValuePair("password", pwd));
			arr.add(new BasicNameValuePair("name", name));
			// 앱을 통한 회원 가입 시 아래 정보는 입력받지 않기 때문에 null로 보낸다.
			arr.add(new BasicNameValuePair("nickname", "N/A"));
			arr.add(new BasicNameValuePair("photo", "N/A"));
			/* */
			arr.add(new BasicNameValuePair("sex", sex));
			arr.add(new BasicNameValuePair("age", birth));
			arr.add(new BasicNameValuePair("job", job));
//			arr.add(new BasicNameValuePair("device_id", 
//																					DeviceInfo.getDevicesUUID(context)));
			arr.add(new BasicNameValuePair("device_id", ""));
			
		} catch (Exception e) {
			ErrorEventSender.sendErrorMsg("- register Err 3\n" + e.getMessage());
			e.printStackTrace();
		}

		int result = 0;
		HttpEntity entity = null;
		try {
			entity = getEntity(arr, "insert.jsp");
			String rslt = EntityUtils.toString(entity).trim();
			
			if(entity != null && rslt.equals("success")) {
				result = OKAY;
			} else if(entity != null && rslt.equals("exist")) {
				result = ALREADY;
			}
			
		} catch (SocketTimeoutException e) {
			ErrorEventSender.sendErrorMsg("- register Err 0\n" + e.getMessage());
			result = NO_NETWORK;
			
		} catch (ConnectTimeoutException e) {
			ErrorEventSender.sendErrorMsg("- register Err 1\n" + e.getMessage());
			result = NO_NETWORK;
			
		} catch (Exception e) {
			ErrorEventSender.sendErrorMsg("- register Err 2\n" + e.getMessage());
			result = ERROR;
		}
		return result;
		
	}

	// 문의하기
	public int qnaMail(String title, String content, StringBuilder msg) {
		if (!checkNetwork(context))
			return NO_NETWORK;

		writeLog("HttpPost : " + content + title);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
			nameValuePairs.add(new BasicNameValuePair("user_qna", title));
			nameValuePairs.add(new BasicNameValuePair("user_tel", content));
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpEntity resEntity = null;
		try {
			resEntity = getEntity(nameValuePairs, "touch/touchif/member/mail.php");
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return NO_NETWORK;
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return NO_NETWORK;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (resEntity != null) {
			InputStream instream;
			try {
				instream = resEntity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				try {
					JSONObject json = new JSONObject(result);
					String state = json.get("state").toString();

					if (state.equals("OK")) {
						msg.append(json.get("msg").toString());
						return OKAY;
					} else {
						msg.append(json.get("msg").toString());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return MathUserNetwork.ERROR;
	}

	public int logout(StringBuilder msg) {

		if (!checkNetwork(context))
			return NO_NETWORK;

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		HttpEntity resEntity = null;
		try {
			resEntity = getEntity(nameValuePairs, "touch/touchif/member/member_user_ifsiu.php");
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return NO_NETWORK;
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return NO_NETWORK;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (resEntity != null) {
			InputStream instream;
			try {
				instream = resEntity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				try {

					JSONObject json = new JSONObject(result);
					String state = json.get("state").toString();
					msg.append(json.get("msg").toString());
					Log.d("State", state);
					if (state == "OK") {
						Log.d("State", "Okay");
						this.setLoggedIn(false);
						return OKAY;
					} else
						Log.d("State", "Okay Fail");

				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return MathUserNetwork.ERROR;
	}

	// 로그인
	public int login(String email, String password, StringBuilder msg) {
		writeLog("StringBuilder msg : " + msg);
		int result = 0;
		if (!checkNetwork(context))
			return NO_NETWORK;

		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			try {
				nameValuePairs.add(new BasicNameValuePair("id", email));
				nameValuePairs.add(new BasicNameValuePair("password", password));
			} catch (Exception e) {
				e.printStackTrace();
			}

			HttpEntity entity = null;
			try {
				entity = getEntity(nameValuePairs, "login.jsp");
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				result = NO_NETWORK;
			} catch (ConnectTimeoutException e) {
				e.printStackTrace();
				result = NO_NETWORK;
			} catch (Exception e) {
				e.printStackTrace();
			}
//			writeLog("받아오는 번호 : " + EntityUtils.toString(entity).trim());
//			writeLog("받아오는 번호 : " + Integer.parseInt(EntityUtils.toString(entity).trim()));
			
			// entity 값을 로그로 파싱하면 Content has been consumed 경고가 발생한다. 하지 않도록.
			if (entity != null && Integer.parseInt(EntityUtils.toString(entity).trim()) > 0) {
//				writeLog("성공시 받아오는 회원 번호 : " + EntityUtils.toString(entity));
				result = OKAY;
			} else {
				result = ERROR;
			}
			
		} catch (Exception e) {
			writeLog("HttpPost got error");
			result = ERROR;
			e.printStackTrace();
			
		}
		writeLog("result : " + result);
		return result;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public DefaultHttpClient getHttpClient() {
		return httpClient;
	}
	
	private void writeLog(String msg) {
		Log.i(PRJT, msg);
	}

}
