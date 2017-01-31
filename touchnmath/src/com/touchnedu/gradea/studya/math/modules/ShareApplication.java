package com.touchnedu.gradea.studya.math.modules;

import com.touchnedu.gradea.studya.math.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ShareApplication {
	Activity activity;
	
	public ShareApplication(Context context) {
		activity = (Activity)context;
	}
	
	public void shareApp() {
		String content = activity.getResources().getString(R.string.share_app);
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
//		intent.putExtra(Intent.EXTRA_SUBJECT, "주제");
		intent.putExtra(Intent.EXTRA_TITLE, "제목");
		intent.putExtra(Intent.EXTRA_TEXT, content);
		activity.startActivity(Intent.createChooser(intent, "친구에게 추천하기"));
		
	}
	
}
