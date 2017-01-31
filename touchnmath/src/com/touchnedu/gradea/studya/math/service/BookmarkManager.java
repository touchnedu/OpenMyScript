package com.touchnedu.gradea.studya.math.service;

import com.touchnedu.gradea.studya.math.data.DbOpenHelper;
import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.util.Util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class BookmarkManager {
	private ImageView bookmarkImgView01, bookmarkImgView02;

	//전역변수 선언
	private Context mContext = null;
	private DbOpenHelper dbManager;
	
	public BookmarkManager(ImageView bookmark01, ImageView bookmark02, Context context) {
		bookmarkImgView01 = bookmark01;
		bookmarkImgView02 = bookmark02;
		this.mContext = context;
		initBookmark();
	}
	
	public void initBookmark() {
		if(bookmarkImgView01 != null) {
			bookmarkImgView01.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(bookmarkImgView01.getVisibility() == View.VISIBLE) {
						bookmarkImgView01.setVisibility(View.INVISIBLE);
						bookmarkImgView02.setVisibility(View.VISIBLE);
						long count = dbManager.insertColumn(DataManager.qQuizNum, 
																											DataManager.qChapterNum);
						/* 이 때 count는 삽입된 row의 id가 리턴됨. */
						if(count > 0)
							callDialog(true);
					}
				}
			});
		}
		if(bookmarkImgView02 != null) {
			bookmarkImgView02.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(bookmarkImgView02.getVisibility() == View.VISIBLE) {
						bookmarkImgView01.setVisibility(View.VISIBLE);
						bookmarkImgView02.setVisibility(View.INVISIBLE);
						boolean result = dbManager.deleteColumn(DataManager.qQuizNum, 
																											DataManager.qChapterNum);
						// 정상적으로 삭제 시 true가 리턴 됨
						if(result)
							callDialog(false);
					}
				}
			});
		}
	}
	
	private void callDialog(boolean b) {
		if(b)
			Util.oneDialog(mContext, "즐겨찾기에 추가되었습니다");
		else
			Util.oneDialog(mContext, "즐겨찾기에서 삭제되었습니다");
	}
	
	public void getBookmarkInfo() {
		dbManager = new DbOpenHelper(mContext);
    dbManager.open();
		
    if(dbManager.isBookmark(DataManager.qQuizNum, DataManager.qChapterNum)) {
    	bookmarkImgView01.setVisibility(View.INVISIBLE);
  		bookmarkImgView02.setVisibility(View.VISIBLE);
  		Log.i("prjt", "북마크 on");
  	} else {
  		bookmarkImgView01.setVisibility(View.VISIBLE);
  		bookmarkImgView02.setVisibility(View.INVISIBLE);
  		Log.i("prjt", "북마크 off");
  	}
	}
	
	public void closeDBManager() {
		dbManager.close();
	}
	
}
