package com.androidstudy.lesson11;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class QuizHelpActivity extends QuizActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.CYAN));

        loadHelpText();
	}

    public void loadHelpText(){
        try {
            InputStream iFile = getResources().openRawResource(R.raw.quizhelp);
            String helpText;
            helpText = inputStreamToString(iFile);
            TextView helpTextBox = (TextView) findViewById(R.id.HelpText);
            helpTextBox.setText(helpText);
            iFile.close();
        } catch (IOException e) {
            Log.e("Help", "InputStreamToString failure", e);
        }
    }

    public String inputStreamToString(InputStream is) throws IOException {
        String text = "";
        BufferedReader reader = new BufferedReader (new InputStreamReader(is));
        String newline = "";
        while ((newline = reader.readLine()) != null){
            text = text + newline + "\n";
        }
        reader.close();
        return text;
    }
}
