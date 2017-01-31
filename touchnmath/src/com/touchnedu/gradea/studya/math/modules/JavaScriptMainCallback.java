package com.touchnedu.gradea.studya.math.modules;

public interface JavaScriptMainCallback {
	public void setAnswer(final String answer);
	public void setScale(final int width, final int height);
	public void setPosition(final int top, final int left);
	public void rightSoundPlay();
	public void finishQuiz();
	public void quizMessage(final String msg);
	public void setCurrentQuizNumber(final String number);
	public void setBackEvent();
	public void getIsNextChapter(final boolean isNextChapter, final String src);
	
}
