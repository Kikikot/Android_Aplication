package com.androidstudy.lesson11;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

public class QuizGameActivity extends QuizActivity {

    public static final String DEBUG_TAG = "GameActivity";

    private TextSwitcher mQuestionText;
    private ImageSwitcher mQuestionImage;

    private SharedPreferences mGameSettings;

    private Hashtable<Integer, Question> mQuestions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);

        mQuestionText = (TextSwitcher) findViewById(R.id.TextSwitcher_QuestionText);
        mQuestionText.setFactory(new MyTextSwitcherFactory());
        mQuestionImage = (ImageSwitcher) findViewById(R.id.ImageSwitcher_QuestionImage);
        mQuestionImage.setFactory(new MyImageSwitcherFactory());

        mGameSettings = getSharedPreferences(GAME_PREFERENCES, Context.MODE_PRIVATE);

        mQuestions = new Hashtable<Integer, Question>(QUESTION_BATCH_SIZE);

        int startingQuestionNumber = mGameSettings.getInt(GAME_PREFERENCES_CURRENT_QUESTION, 1);

    /*  This piece of code is to build the game again, and restart the game status.

        int startingQuestionNumber = 1;
        SharedPreferences.Editor editor = mGameSettings.edit();
        editor.putInt(GAME_PREFERENCES_CURRENT_QUESTION, startingQuestionNumber);
        editor.putInt(GAME_PREFERENCES_SCORE, 0);
        editor.commit();
    */

        try{
            loadQuestionBatch(startingQuestionNumber);
        } catch (Exception e){
            Log.e(DEBUG_TAG, "Loading initial question batch failed", e);
        }

        // If the question was loaded properly, display it
        if (mQuestions.containsKey(startingQuestionNumber)) {

            Log.i(DEBUG_TAG, "Lo contiene de primeras");
            // Set the text of the textswitcher
            mQuestionText.setCurrentText(getQuestionText(startingQuestionNumber));
            // Set the image of the imageswitcher
            Drawable image = getQuestionImageDrawable(startingQuestionNumber);
            mQuestionImage.setImageDrawable(image);
        } else {
            Log.i(DEBUG_TAG, "No lo contiene");
            // Tell the user we don't have any new questions at this time
            handleNoQuestions();
        }
	}

    private void handleNoQuestions() {

        mQuestionText.setText(getResources().getText(R.string.no_questions));

        mQuestionImage.setImageResource(R.drawable.noquestion);
        Button yesButton =
                (Button) findViewById(R.id.Button_Yes);
        yesButton.setEnabled(false);
        Button noButton =
                (Button) findViewById(R.id.Button_No);
        noButton.setEnabled(false);
    }

    public void onNoButton(View v) {
        handleAnswerAndShowNextQuestion(false);
    }

    public void onYesButton(View v) {
        handleAnswerAndShowNextQuestion(true);
    }

    private void handleAnswerAndShowNextQuestion(boolean bAnswer) {
        int curScore = mGameSettings.getInt(GAME_PREFERENCES_SCORE, 0);
        int nextQuestionNumber =  mGameSettings.getInt(GAME_PREFERENCES_CURRENT_QUESTION, 1) + 1;

        SharedPreferences.Editor editor = mGameSettings.edit();
        editor.putInt(GAME_PREFERENCES_CURRENT_QUESTION, nextQuestionNumber);
        if (bAnswer) {
            editor.putInt(GAME_PREFERENCES_SCORE, curScore + 1);
        }
        editor.commit();

        if (!mQuestions.containsKey(nextQuestionNumber)) {
            // Load next batch
            try {
                loadQuestionBatch(nextQuestionNumber);
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "Loading updated question batch failed", e);
            }
        }
        if (mQuestions.containsKey(nextQuestionNumber)) {
            // Update question text
            TextSwitcher questionTextSwitcher =
                    (TextSwitcher) findViewById(R.id.TextSwitcher_QuestionText);
            questionTextSwitcher.setText(getQuestionText(nextQuestionNumber));
            // Update question image
            ImageSwitcher questionImageSwitcher =
                    (ImageSwitcher) findViewById(R.id.ImageSwitcher_QuestionImage);
            Drawable image = getQuestionImageDrawable(nextQuestionNumber);
            questionImageSwitcher.setImageDrawable(image);
        } else {
            handleNoQuestions();
        }
    }

    private void loadQuestionBatch(int startQuestionNumber) throws XmlPullParserException, IOException {
        // Remove old batch
        mQuestions.clear();
        // TODO: Contact the server and retrieve a batch of question data, beginning at startQuestionNumber
        XmlResourceParser questionBatch;
        // BEGIN MOCK QUESTIONS
        if (startQuestionNumber < 16) {
            questionBatch = getResources().getXml(R.xml.samplequestions);
        } else {
            questionBatch = getResources().getXml(R.xml.samplequestions2);
        }

        // END MOCK QUESTIONS
        // Parse the XML
        int eventType = -1;
        int x = 0;
        // Find Score records from XML
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            if (eventType == XmlResourceParser.START_TAG) {
                // Get the name of the tag (eg questions or question)
                String strName = questionBatch.getName();
                if (strName.equals(XML_TAG_QUESTION)) {
                    String questionNumber = questionBatch.getAttributeValue(null,
                            XML_TAG_QUESTION_ATTRIBUTE_NUMBER);
                    Integer questionNum = new Integer(questionNumber);
                    String questionText = questionBatch.getAttributeValue(null,
                            XML_TAG_QUESTION_ATTRIBUTE_TEXT);
                    String questionImageUrl = questionBatch.getAttributeValue(null,
                            XML_TAG_QUESTION_ATTRIBUTE_IMAGEURL);
                    // Save data to our hashtable
                    mQuestions.put(questionNum, new Question(questionNum, questionText,
                            questionImageUrl));
                    x++;
                }
            }
            eventType = questionBatch.next();
        }
        Log.i(DEBUG_TAG, "Se han añadido "+x+" preguntas");
    }

    private String getQuestionText(Integer questionNumber) {
        String text = null;
        Question curQuestion = mQuestions.get(questionNumber);
        if (curQuestion != null) {
            text = curQuestion.mText;
        }
        Log.i(DEBUG_TAG, "Se ha creado el texto");
        return text;
    }

    private String getQuestionImageUrl(Integer questionNumber) {
        String url = null;
        Question curQuestion = mQuestions.get(questionNumber);
        if (curQuestion != null) {
            url = curQuestion.mImageUrl;
        }
        Log.i(DEBUG_TAG, "Se ha rescatado la url");
        return url;
    }

    private Drawable getQuestionImageDrawable(int questionNumber) {
        Drawable image;
        URL imageUrl;
        try {
            imageUrl = new URL(getQuestionImageUrl(questionNumber));
            InputStream stream = imageUrl.openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            image = new BitmapDrawable(getResources(), bitmap);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Decoding Bitmap stream failed");
            image = getResources().getDrawable(R.drawable.noquestion);
        }
        Log.i(DEBUG_TAG, "Se ha cargado una imagen");
        return image;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.gameoptions, menu);
		menu.findItem(R.id.help_menu_item).setIntent(new Intent(this, QuizHelpActivity.class));
		menu.findItem(R.id.settings_menu_item).setIntent( new Intent(this, QuizSettingsActivity.class));
		menu.findItem(R.id.scores_menu_item).setIntent(new Intent(this, QuizScoresActivity.class));

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		startActivity(item.getIntent());
		return true;
	}

    private class MyTextSwitcherFactory implements ViewSwitcher.ViewFactory {
        @Override
        public TextView makeView() {
            TextView textView = (TextView) LayoutInflater.from(
                    getApplicationContext()).inflate(
                    R.layout.text_switcher_view,
                    mQuestionText, false);
            return textView;
        }
    }

    private class MyImageSwitcherFactory implements ViewSwitcher.ViewFactory {
        @Override
        public ImageView makeView() {
            ImageView imageView = (ImageView) LayoutInflater.from(
                    getApplicationContext()).inflate(
                    R.layout.image_switcher_view,
                    mQuestionImage, false);
            return imageView;
        }
    }

    private class Question {
        int mNumber;
        String mText;
        String mImageUrl;
        public Question(int questionNum, String questionText, String
                questionImageUrl) {
            mNumber = questionNum;
            mText = questionText;
            mImageUrl = questionImageUrl;
        }
    }
}
