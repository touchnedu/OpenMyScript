package com.touchnedu.gradea.studya.math.util;

import java.util.UUID;

import com.touchnedu.gradea.studya.math.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Util {

	private static String deviceId = null;;

	/**
	 * 디바이스 아이디 가져오기
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context){
		if(deviceId == null){
			final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			final String tmDevice, tmSerial, androidId;
			tmDevice = "" + tm.getDeviceId();
			tmSerial = "" + tm.getSimSerialNumber();
			androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

			UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());

			deviceId = deviceUuid.toString();
		}
        return deviceId;
	}

	/**
	 * 알림창
	 * @param context
	 * @param msg
	 */
	public static void showAlert(Context context, String msg){
		showAlert(context, msg, "확인", null, null, null);
	}

	public static void showAlert(Context context, String msg, String positiveBtn, 
													DialogInterface.OnClickListener positiveBtnListener){
		showAlert(context, msg, positiveBtn, positiveBtnListener, null, null);
	}

	public static void showAlert(Context context, String msg, String positiveBtn, 
												DialogInterface.OnClickListener positiveBtnListener, 
												String negativeBtn, 
												DialogInterface.OnClickListener negativeBtnListener) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle("터치앤매쓰 알림");
		ad.setPositiveButton(positiveBtn, positiveBtnListener);
		if(negativeBtn != null){
			ad.setNegativeButton(negativeBtn, negativeBtnListener);
		}
		ad.setMessage(msg).show();
	}
	
	public static void log(String msg){
		Log.d("minu", msg);
	}

	// 네트워크체크
	public static boolean networkCheck(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.
																getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean result = false;

		NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wimax = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

		if (wimax == null) {
			if (mobile.isConnected() || wifi.isConnected()) {
				result = true;
			}
		} else {
			if (mobile.isConnected() || wifi.isConnected() || wimax.isConnected()) {
				result = true;
			}
		}
		return result;
	}

	// 가상키보드 컨트롤	( 0 : 숨기기 / 1 : 보이기 )
	public void keyBoardControl(Context context, EditText et, int i){
		Context kContext = context;
		EditText kEt = et;
		InputMethodManager imm = (InputMethodManager) kContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		switch (i) {
		case 0:
			imm.hideSoftInputFromWindow(kEt.getWindowToken(), 0);
			break;
		case 1:
			imm.showSoftInput(kEt, InputMethodManager.SHOW_IMPLICIT);
			break;
		}
	}

	// 오류창 띄우고 종료하기 
	public static void confirmOneDialog(final Context context, String msg) {
		AlertDialog myDialog = 
				 new AlertDialog.Builder(context).setMessage(msg)
				  							.setPositiveButton(context.getString(R.string.end_yes), 
				  																 new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				((Activity)context).finish();
			}
		}).create();
		myDialog.show();
	}

	// 확인창 띄우기
	public static void oneDialog(final Context context, String msg) {
		AlertDialog myDialog = new AlertDialog.Builder(context).setMessage(msg)
				 								.setPositiveButton(context.getString(R.string.ad_yes), 
				 																 new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		}).create();
		myDialog.show();
	}

	public static boolean confirmAlertDialog(final Context context, 
			 																							String title, String msg) {
		 AlertDialog myDialog = new AlertDialog.Builder(context).setTitle(title)
				 								.setMessage(msg)
				 								.setPositiveButton(context.getString(R.string.ad_yes), 
				 																 new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		}).create();
		myDialog.show();
		return true;
	}
	
	public static void customOneDialog(final Context con, String title, 
																					String msg, int id, final boolean b) {
		final Dialog cDialog = new Dialog(con, R.style.FullHeightDialog);
		cDialog.setContentView(id);
		cDialog.show();
		TextView cTitle = (TextView)cDialog.findViewById(R.id.dialog_title);
		TextView content = (TextView)cDialog.findViewById(R.id.dialog_content);
		Button cButton = (Button)cDialog.findViewById(R.id.dialog_btn_ok);
		content.setText(msg);
		if(!title.equals(""))
			cTitle.setText(title);
		
		cButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cDialog.dismiss();
				if(b)
					((Activity)con).finish();
			}
		});
	}
	
	public static void logoutDialog(final Context context, final boolean isNaver) {
		final Dialog logoutDialog = new Dialog(context, R.style.FullHeightDialog);
		logoutDialog.setContentView(R.layout.custom_dialog_one);
		logoutDialog.show();
		TextView content = (TextView)logoutDialog.findViewById(R.id.dialog_content);
		Button button = (Button)logoutDialog.findViewById(R.id.dialog_btn_ok);
		content.setText(R.string.dialog_logout);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logoutDialog.dismiss();
				if(isNaver)
					((Activity)context).finish();
			}
		});
		
		
	}
	
	public static void finishApp(final Context context) {
		final Dialog cDialog = new Dialog(context, R.style.FullHeightDialog);
		cDialog.setContentView(R.layout.custom_dialog_quit);
		cDialog.show();
		Button cButtonNO = (Button)cDialog.findViewById(R.id.dialog_btn_cancel);
		Button cButtonYes = (Button)cDialog.findViewById(R.id.dialog_btn_ok);
		cButtonNO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cDialog.dismiss();
			}
		});
		cButtonYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((Activity)context).finish();
			}
		});
		
	}

	// 발신
	public static void adCall(Context context, String telNum){
		context.startActivity(new Intent("android.intent.action.DIAL", 
																		 Uri.parse("tel:"+telNum))); // "tel:010-0000-0000"
	}

	// 문자전송
	public static void adSms(Context context, String telNum){

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        Uri uri = Uri.parse("sms:" + telNum);
        intent.setData(uri);
        intent.putExtra("sms_body", "문의 : ");
        context.startActivity(intent);
	}
	//  메일문의
	public static void mail(Context mContext, String email) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
//	String[] recipients = new String[] {email, };
//	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "제목");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "내용 : ");
		try {
			mContext.startActivity(Intent.createChooser(emailIntent, "Send Mail"));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(mContext, "There are no email clients installed", 
																									Toast.LENGTH_SHORT).show();
		}
	}

	// 텍스트뷰 미드라인
	public static void setStrikethroughSpan(TextView v, String text){
		SpannableString content = new SpannableString(text);
		content.setSpan(new StrikethroughSpan(), 0, content.length(), 0);
		v.setText(content);
	}
	// 텍스트뷰 언더라인
	public static void setUnderlineSpann(TextView v, String text){
		SpannableString content = new SpannableString(text);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		v.setText(content);
	}

	//  dp to px
	public static int DpToPixel(Context context, int DP) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
															DP, context.getResources().getDisplayMetrics());
	}

	public static String chapterConvertPlus(String number) {
		int subStringNumber = Integer.parseInt(number.substring(5)) + 1;
		String result = "";
		if(subStringNumber < 10) {
			result = "0" + subStringNumber;
		} else {
			result = "" + subStringNumber;
		}
		return number.substring(0, 5) + result;
	}
	
	public static String setNumberStyle(int number) {
		if(number < 10) 
			return "0" + number;
		else 
			return "" + number;
	}
	
}
