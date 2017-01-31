package com.touchnedu.gradea.studya.math.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class DBInsert extends Activity {

	private static DbOpenHelper mDbOpenHelper;
	public static Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		mContext = this;
		Intent intent = getIntent();
		String hintCode = intent.getStringExtra("hintCode");
		String count = intent.getStringExtra("count");
		String chapter = intent.getStringExtra("chapter");

		mDbOpenHelper = new DbOpenHelper(this);
		mDbOpenHelper.open();

		Log.d("DBCreate insertDB hintCode :: ", hintCode);
		Log.d("DBCreate insertDB count :: ", count);
		Log.d("DBCreate insertDB chapter :: ", chapter);

		// 북 마크 입력
		mDbOpenHelper.deleteColumn(hintCode, chapter);
		mDbOpenHelper.insertColumn(hintCode, chapter);
		mDbOpenHelper.close();
		finish();
	}
}