package com.touchnedu.gradea.studya.math.util;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class stringUtil {

	/*
	 * 전화번호 자릿수 자르기
	 */
	public static String telstr(String telNumber) {
		String phoneNumber = "";

		if(telNumber != null) {
			// KT 특이 사항
			telNumber = telNumber.replace("+82", "0");
		}

		int telnum = telNumber.length();

		if(telnum > 0) {

			if(telnum == 11) {
				phoneNumber = telNumber.substring(0, 3) + "-"+ telNumber.substring(3, 7) + "-"+ telNumber.substring(7, 11);
			} else if (telnum == 10) {
				phoneNumber = telNumber.substring(0, 2);
				if(phoneNumber.equals("02")) {
					phoneNumber = telNumber.substring(0, 2) + "-"+ telNumber.substring(2, 6) + "-"+ telNumber.substring(6, 10);
				} else {
					phoneNumber = telNumber.substring(0, 3) + "-"+ telNumber.substring(3, 6) + "-"+ telNumber.substring(6, 10);
				}
			} else if (telnum == 9) {
				if(phoneNumber.equals("02")) {
					phoneNumber = telNumber.substring(0, 2) + "-"+ telNumber.substring(2, 5) + "-"+ telNumber.substring(5, 9);
				}
			} else if (telnum == 8) { // 1588-1588
				phoneNumber = telNumber.substring(0, 4) + "-"+ telNumber.substring(4, 8);
			}

		} else {
			phoneNumber = "유효하지 않는 번호 입니다.";
		}

		return phoneNumber;
	}

	// 시간 포맷
	public static String timeToString(Long time) {
	    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String date = simpleFormat.format(new Date(time));
	    return date;
	}

	// 현재 날짜
	public static String timeToDayString() {
	    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
	    String date = simpleFormat.format(new Date(System.currentTimeMillis()));
	    Util.log(" CashBell date == " + date);
	    return date;
	}

	public static String timeToDayStr(String value) {
	    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
	    String date = simpleFormat.format(new Date(System.currentTimeMillis()));
	    Util.log(" CashBell date == " + date);
	    return date;
	}


	// 현재 날짜시분초
	public static String getToday() {
	    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
	    String date = simpleFormat.format(new Date(System.currentTimeMillis()));
	    Util.log(" CashBell date == " + date);
	    return date;
	}

	//  자릿수 천단위 포멧
	public static String setMoneyForm(String value){
		if(value.length()>3){
			 int result = Integer.parseInt(value);
			 return new java.text.DecimalFormat("#,###").format(result);
		}else{
			return value;
		}
	}




	// 휴대전화번호 체크
	public static boolean phoneNumChk(String str) {
		boolean result = false;
//		String phoneText = join_phone_editText.getText().toString().replace(" ", "");
		if(str.length()>9 && str.length()<12 ){
			if(str.substring(0,3).equals("010") || str.substring(0,3).equals("019") || str.substring(0,3).equals("017") || str.substring(0,3).equals("011")){
				result = true;
			}
		}
		return result;
	}
	// 인증번호 체크
	public static boolean returnNumChk(String str) {
		boolean result = false;
		if(str.length()>0 && str.length()<9 ){
			result = true;
		}
		return result;
	}
	// num 1개이상 존재함 true
	public static boolean isNumber(String str){
		boolean result = false;
		if (str.equals("")) {
			result = false;
		}
		for (int i = 0; i < str.length(); i++) {
			int c = str.charAt(i);
			if (Character.isDigit(c)) {
				result = true;
				break;
			}
		}
		return result;
	}
	// string(소문자) 1개이상 존재함 true
	public static boolean isLower(String str){
		boolean result = false;
		if (str.equals("")) {
			result = false;
		}
		for (int i = 0; i < str.length(); i++) {
			int c = str.charAt(i);
			if (Character.isLowerCase(c)) {
				result = true;
				break;
			}
		}
		return result;
	}
	// string(대문자) 1개이상 존재함 true
	public static boolean isUpper(String str){
		boolean result = false;
		if (str.equals("")) {
			result = false;
		}
		for (int i = 0; i < str.length(); i++) {
			int c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				result = true;
				break;
			}
		}
		return result;
	}











}
