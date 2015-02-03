package com.androidstudy.lesson11;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class QuizActivity extends Activity {
	
	public static final String GAME_PREFERENCES = "GamePrefs";

    public static final String GAME_PREFERENCES_NICKNAME = "Nickname"; // String
    public static final String GAME_PREFERENCES_EMAIL = "Email"; // String
    public static final String GAME_PREFERENCES_PASSWORD = "Password"; // String
    public static final String GAME_PREFERENCES_DOB = "DOB"; // Long
    public static final String GAME_PREFERENCES_GENDER = "Gender"; // Int
    public static final String GAME_PREFERENCES_SCORE = "Score";
    public static final String GAME_PREFERENCES_CURRENT_QUESTION = "CurQuestion";

    public static final String XML_TAG_QUESTION_BLOCK = "questions";
    public static final String XML_TAG_QUESTION = "question";
    public static final String XML_TAG_QUESTION_ATTRIBUTE_NUMBER = "number";
    public static final String XML_TAG_QUESTION_ATTRIBUTE_TEXT = "text";
    public static final String XML_TAG_QUESTION_ATTRIBUTE_IMAGEURL = "imageUrl";

    public static final int QUESTION_BATCH_SIZE = 15;

	private String TAG = "Quiz (L11)";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showLog("onCreate");
	}
	protected void onStart() {
		super.onStart();
		showLog("onStart");
	}
	protected void onResume() {
		super.onResume();
		showLog("onResume");
	}
	protected void onPause() {
		super.onPause();
		showLog("onPause");
	}
	protected void onStop() {
		super.onStop();
		showLog("onStop");
	}
	protected void onDestroy() {
		super.onDestroy();
		showLog("onDestroy");
	}
	protected void onRestart() {
		super.onRestart();
		showLog("onRestart");
	}
	
	public void bMenuPressed(View v){
		finish();
	}

	private void showLog(String running) {
		Log.i(TAG, "Arriving to '" + running + "()' in '" + getWhere() + "'");
	}

	private String getWhere() {
		String where = this.getClass().getName();
		int lastWord = where.lastIndexOf('.');
		where = where.substring(lastWord+1);
		return where;
	}
	
}
