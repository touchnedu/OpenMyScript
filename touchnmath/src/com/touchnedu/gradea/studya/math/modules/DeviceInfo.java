package com.touchnedu.gradea.studya.math.modules;

import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceInfo {

	public static String getPackageName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager()
															.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
			
		} catch(NameNotFoundException e) {
			e.printStackTrace();
			return null;
			
		}
	}
	
	public static int getApiVersionNumber() {
		return android.os.Build.VERSION.SDK_INT;
	}
	
	public static String getDevicesUUID(Context context) {
		/* 아래 방법은 2.2 버전 이하에서 사용한다. */
		final TelephonyManager tm = (TelephonyManager)context
																	 .getSystemService(Context.TELEPHONY_SERVICE);
		
//		final String tmDevice, tmSerial, androidId;
//		tmDevice = "" + tm.getDeviceId();
//		tmSerial = "" + tm.getSimSerialNumber();
//		androidId = "" + android.provider.Settings.Secure.getString(
//																		context.getContentResolver(), 
//																		android.provider.Settings.Secure.ANDROID_ID);
		
		/* 시프트 연산자 << : 왼쪽으로 32비트 이동
		 * 비트 연산자 | : 양쪽 비트 중 어느 하나라도 1이면 결과가 1.
		 * -> 101 | 100 = 101
		 * */
//		UUID deviceUuid = new UUID(androidId.hashCode(), 
//											((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		
//		String deviceUniqueId = deviceUuid.toString();
//		return deviceUniqueId; // deviceUuid를 string형으로 변환해서 리턴하면 된다.
		
		/* 일반적으로는 아래 방법으로 간단하게 id를 획득할 수 있다. */
		String uniqueId = "";
		if(!checkPermission(context)) {
			return uniqueId;
		}
		
		if(tm.getDeviceId() != null) {
			uniqueId = tm.getDeviceId();
		} else {
			uniqueId = Secure.getString(context.getContentResolver(), 
																										Settings.Secure.ANDROID_ID); 
		}
		
		return uniqueId;
	}

	/* 디바이스 정보를 확인하여 태블릿인지 아닌지의 여부를 리턴 */
	public static boolean checkTabletDeviceWithProperties() {
		try {
			InputStream is = Runtime.getRuntime()
															.exec("getprop ro.build.characteristics")
															.getInputStream();
			byte[] bytes = new byte[1024];
			is.read(bytes);
			is.close();
			
			String byteValue = new String(bytes).toLowerCase(Locale.getDefault());
			boolean isTablet = byteValue.contains("tablet");
			Log.i("prjt", "디바이스 기기는 : " + byteValue);
			return isTablet;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/* READ_PHONE_STATE에 대한 권한 획득 여부 */
	private static boolean checkPermission(Context context) {
		int permissionNumber = ContextCompat.checkSelfPermission(context, 
																					Manifest.permission.READ_PHONE_STATE);
		if(permissionNumber == PackageManager.PERMISSION_DENIED) {
			return false;
		} else {
			return true;
		}
	}
	
}
