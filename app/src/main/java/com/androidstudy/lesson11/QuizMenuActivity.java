package com.androidstudy.lesson11;

import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class QuizMenuActivity extends QuizActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
		
		TextView lastConnection = (TextView)findViewById(R.id.tLastConnection);
		SharedPreferences settings = getSharedPreferences(GAME_PREFERENCES, MODE_PRIVATE);
		lastConnection.setText(settings.getString("LastDate", "First Connection"));
		
		ListView menuList = (ListView) findViewById(R.id.OptionsList);
		
	/*	String[] items = { getResources().getString(R.string.menu_item_play),
				getResources().getString(R.string.menu_item_scores),
				getResources().getString(R.string.menu_item_settings),
				getResources().getString(R.string.menu_item_help),
				getResources().getString(R.string.menu_item_exit)};
	*/	
		
		String[] items = getResources().getStringArray(R.array.items);
		
		
		ArrayAdapter<String> adapt = new ArrayAdapter<String>(this,
				R.layout.menu_item, items);
		
		menuList.setAdapter(adapt);
		
		menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id){
				TextView textView = (TextView) itemClicked;
				String strText = textView.getText().toString();
				if (is(strText, R.string.menu_item_play)){
					// Launch the Game Activity
					bItemPressed(QuizGameActivity.class);
				} else if (is(strText, R.string.menu_item_help)){
					// Launch the Help Activity
					bItemPressed(QuizHelpActivity.class);
				} else if (is(strText, R.string.menu_item_settings)){
					// Launch the Settings Activity
					bItemPressed(QuizSettingsActivity.class);
				} else if (is(strText, R.string.menu_item_scores)){
					// Launch the Scores Activity
					bItemPressed(QuizScoresActivity.class);
				}else if (is(strText, R.string.menu_item_exit)){
					// Launch the Scores Activity
					bExitPressed();
				}
			}

			private boolean is(String strText, int menuItem) {
				return strText.equalsIgnoreCase(getResources().getString(menuItem));
			}
			private void bItemPressed(Object class1){
				startActivity(new Intent(getApplicationContext(), (Class)class1));
			}
		});
	}
	
	public void bExitPressed(){
		finish();
	}
	
	protected void onDestroy(){
		// Saving now like Last Connection
		SharedPreferences settings = getSharedPreferences(GAME_PREFERENCES, MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settings.edit();
		DateFormat df = new DateFormat();
		String dateS = df.format("dd/MM/yy - hh:mm", new Date())+""; 
		prefEditor.putString("LastDate", dateS);
		prefEditor.commit();
		super.onDestroy();
	}
}
