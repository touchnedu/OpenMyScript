package com.touchnedu.gradea.studya.math.modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class SendMailToContact {
	Activity activity;
	
	public SendMailToContact(Context context) {
		activity = (Activity)context;
	}
	
	public void showBrowser() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		
		String[] tos = { "contact@touchnedu.com" };
		intent.putExtra(Intent.EXTRA_EMAIL, tos);
		intent.putExtra(Intent.EXTRA_SUBJECT, "");
		intent.putExtra(Intent.EXTRA_TEXT, "");
		activity.startActivity(intent);
		
	}
	
}
