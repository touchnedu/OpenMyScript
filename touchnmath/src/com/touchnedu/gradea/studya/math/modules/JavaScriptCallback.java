package com.touchnedu.gradea.studya.math.modules;

public interface JavaScriptCallback {
	public void openQuizActivity(final String code);
	public void setChapterTitle(final String title);
	public void notReadyContent();
	public void getHintContentInfo(String title, String gCode, String cCode);
}
