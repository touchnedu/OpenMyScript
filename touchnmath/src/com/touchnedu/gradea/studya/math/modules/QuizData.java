package com.touchnedu.gradea.studya.math.modules;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class QuizData {
	private int id = 0;
	private int chapterId = 1;
	private int curQuizNum = 1;
	ArrayList<Integer> quizNumber;
	ArrayList<String> chapterText;

	public int quizDataNum; // 퀴즈 문제번호
	public String quizDataStr; // 퀴즈문제명
	public String hintCode; // 힌트코드
	public String chapterStr; // 소챕터
	
	Context context;
	private static QuizData qData;
	
	public QuizData() {
		setId(curQuizNum);
		setQuiz();
	}
	
	public static QuizData getInstance() {
		if(qData == null) {
			qData = new QuizData();
		}
		return qData;
	}
	
	public void initData() {
		setId(curQuizNum);
		setQuiz();
	}
	
	public int prevQuiz(Context context) {
		if(curQuizNum > 1) {
			setId(--curQuizNum);
			return 1;
		} else {
			Toast.makeText(context, "첫 번째 문제입니다.", Toast.LENGTH_SHORT).show();
			return 0;
		}
	}
	
	public int nextQuiz(Context context) {
		if(curQuizNum < quizNumber.size()) {
			setId(++curQuizNum);
			return 1;
		} else {
			Toast.makeText(context, "마지막 문제입니다.", Toast.LENGTH_SHORT).show();
			return 0;
		}
	}
	
	public void setQuiz() {
		
		Log.d("setQuiz quizDataNum :: ", String.valueOf(quizDataNum));
		
		quizNumber = new ArrayList<Integer>();
		for(int i = 1; i < quizDataNum+1; i++) {
			quizNumber.add(i);
			Log.d("setQuiz quizDataNum  for i :: ", String.valueOf(i));
		}
		
		chapterText = new ArrayList<String>();
		
		for(int j = 1; j < quizDataNum+1; j++) {
			chapterText.add(quizDataStr);
			Log.d("chapterText.add(quizDataStr)  for j :: ", String.valueOf(j));
			Log.d("chapterText.add(quizDataStr)  for j :: ", quizDataStr);
		}
		/*chapterText.add("Ⅰ-5. 공배수와 최소공배수");
		chapterText.add("Ⅴ-5. 다각형");
		chapterText.add("Ⅰ-(2)-9. 정수와 유리수의 혼합계산");
		chapterText.add("Ⅰ-5. 공배수와 최소공배수");
		chapterText.add("Ⅴ-5. 다각형");
		chapterText.add("Ⅰ-(2)-9. 정수와 유리수의 혼합계산");
		chapterText.add("Ⅰ-5. 공배수와 최소공배수");
		chapterText.add("Ⅴ-5. 다각형");*/
		
		
	}
	
	public String getChapterText() {
		return chapterText.get(getId() - 1);
	}
	
	public void setId(int curQuizNum) {
		id = curQuizNum;
	}
	
	public int getId() {
		return id;
	}

	public void setChapterId() {
		/** 차후 구현해야 함. */
	}
	
	public int getChapterId() {
		/** 차후 구현해야 함. */
		return chapterId;
	}
	
	public int getQuizDataNum() {
		return quizDataNum;
	}

	public void setQuizDataNum(int quizDataNum) {
		this.quizDataNum = quizDataNum;
	}

	public String getQuizDataStr() {
		return quizDataStr;
	}

	public void setQuizDataStr(String quizDataStr) {
		this.quizDataStr = quizDataStr;
	}
	
	public String getHintCode() {
		return hintCode;
	}

	public void setHintCode(String hintCode) {
		this.hintCode = hintCode;
	}

	public String getChapterStr() {
		return chapterStr;
	}

	public void setChapterStr(String chapterStr) {
		this.chapterStr = chapterStr;
	}
	
}
