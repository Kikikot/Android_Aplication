package com.androidstudy.lesson11;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class QuizMenuActivity extends QuizActivity {

    static final int RESET_DIALOG = 0;
    private SharedPreferences mGameSettings;

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
				} else if (is(strText, R.string.menu_item_reset_game)){
                    // Launch the Reset score Dialog
                    showDialog(RESET_DIALOG);
                } else if (is(strText, R.string.menu_item_exit)){
					// Finish the aplication
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case RESET_DIALOG:
                mGameSettings = getSharedPreferences(GAME_PREFERENCES, Context.MODE_PRIVATE);
                int score = mGameSettings.getInt(GAME_PREFERENCES_SCORE, 0);
                int questionNumber = mGameSettings.getInt(GAME_PREFERENCES_CURRENT_QUESTION, 1);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You are at question "+questionNumber+"\n" +
                        "and your score is "+score+"\nDo you really want to restart them?");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = mGameSettings.edit();
                        editor.putInt(GAME_PREFERENCES_CURRENT_QUESTION, 1);
                        editor.putInt(GAME_PREFERENCES_SCORE, 0);
                        editor.commit();
                        Toast.makeText(QuizMenuActivity.this, "Data have been reset", Toast.LENGTH_LONG).show();
                        QuizMenuActivity.this.removeDialog(RESET_DIALOG);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                QuizMenuActivity.this.removeDialog(RESET_DIALOG);
                            }
                        });
                AlertDialog resetDialog = builder.create();
                return resetDialog;
        }
        return null;
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
