package com.androidstudy.lesson11;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class QuizSettingsActivity extends QuizActivity {

    private SharedPreferences mGameSettings;
    private Spinner spinner;
    private EditText nicknameText;
    private EditText emailText;
    private TextView textPass;
    private TextView textBirth;

    static final int DATE_DIALOG_ID = 0;
    static final int PASSWORD_DIALOG_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        initResources();
	}

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DATE_DIALOG_ID:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dateDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener(){
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                                Time dateOfBirth = new Time();
                                dateOfBirth.set(dayOfMonth, monthOfYear, year);
                                long dtDob = dateOfBirth.toMillis(true);
                                String DOB = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(dtDob);
                                textBirth.setText(DOB);
                                SharedPreferences.Editor editor = mGameSettings.edit();
                                editor.putLong(GAME_PREFERENCES_DOB, dtDob);
                                editor.commit();
                            }
                        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
                );
                return dateDialog;
            case PASSWORD_DIALOG_ID:
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.password_dialog, (ViewGroup) findViewById(R.id.root));
                final EditText p1 = (EditText) layout.findViewById(R.id.tPass);
                final EditText p2 = (EditText) layout.findViewById(R.id.tConfirmPass);
                final TextView error = (TextView) layout.findViewById(R.id.tPassSMS);
                p2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String strPass1 = p1.getText().toString();
                        String strPass2 = p2.getText().toString();
                        if (strPass1.equals(strPass2)){
                            error.setText(R.string.settings_pass_OK);
                            error.setTextColor(Color.BLUE);
                        }else{
                            error.setText(R.string.settings_pass_KO);
                            error.setTextColor(Color.RED);
                        }
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(layout);
                builder.setTitle(R.string.settings_pass);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //textPass
                                String strPassword1 = p1.getText().toString();
                                String strPassword2 = p2.getText().toString();
                                if (strPassword1.equals(strPassword2)) {
                                    SharedPreferences.Editor editor = mGameSettings.edit();
                                    editor.putString(GAME_PREFERENCES_PASSWORD, strPassword1);
                                    editor.commit();
                                    textPass.setText(R.string.settings_pass_set);
                                } else {
                                    Log.d("DEBUG_TAG", "Passwords do not match. Not saving. Keeping old password (if set).");
                                }
                                QuizSettingsActivity.this
                                        .removeDialog(PASSWORD_DIALOG_ID);
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                QuizSettingsActivity.this
                                        .removeDialog(PASSWORD_DIALOG_ID);
                            }
                        });
                AlertDialog passwordDialog = builder.create();
                return passwordDialog;
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog dateDialog = (DatePickerDialog) dialog;
                int iDay, iMonth, iYear;
                if (mGameSettings.contains(GAME_PREFERENCES_DOB)) {
                    long msBirthDate = mGameSettings.getLong(GAME_PREFERENCES_DOB, 0);
                    Time dateOfBirth = new Time();
                    dateOfBirth.set(msBirthDate);
                    iDay = dateOfBirth.monthDay;
                    iMonth = dateOfBirth.month;
                    iYear = dateOfBirth.year;
                } else {
                    Calendar cal = Calendar.getInstance();
                    iDay = cal.get(Calendar.DAY_OF_MONTH);
                    iMonth = cal.get(Calendar.MONTH);
                    iYear = cal.get(Calendar.YEAR);
                }
                dateDialog.updateDate(iYear, iMonth, iDay);
                return;
        }
    }

    private void initResources() {
        mGameSettings = getSharedPreferences(GAME_PREFERENCES, Context.MODE_PRIVATE);
        spinner = (Spinner) findViewById(R.id.spinner);
        initSpinner();
        nicknameText = (EditText) findViewById(R.id.EditText_Nickname);
        initNicknameText();
        emailText = (EditText) findViewById(R.id.EditText_Email);
        initEmailText();
        textPass = (TextView) findViewById(R.id.textPass);
        initPass();
        textBirth = (TextView) findViewById(R.id.textBirth);
        initBirth();
    }

    private void initBirth() {
        if (mGameSettings.contains(GAME_PREFERENCES_DOB)) {
            long Dob = mGameSettings.getLong(GAME_PREFERENCES_DOB, 0);
            String DOB = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(Dob);
            textBirth.setText(DOB);
        } else {
            textBirth.setText(R.string.settings_birth_not_set);
        }
    }

    private void initPass() {
        if (mGameSettings.contains(GAME_PREFERENCES_PASSWORD)) {
            textPass.setText(R.string.settings_pass_set);
        }
    }

    private void initEmailText() {
        if (mGameSettings.contains(GAME_PREFERENCES_EMAIL)) {
            emailText.setText(mGameSettings.getString(
                    GAME_PREFERENCES_EMAIL, ""));
        }
        emailText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (KeyEvent.ACTION_DOWN == event.getAction()) {
                            SharedPreferences.Editor editor = mGameSettings.edit();
                            editor.putString(GAME_PREFERENCES_EMAIL, emailText.getText().toString());
                            editor.commit();
                            return true;
                        }
                }
                return false;
            }
        });
    }

    private void initNicknameText() {
        if (mGameSettings.contains(GAME_PREFERENCES_NICKNAME)) {
            nicknameText.setText(mGameSettings.getString(
                    GAME_PREFERENCES_NICKNAME, ""));
        }
        nicknameText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (KeyEvent.ACTION_DOWN == event.getAction()) {
                            SharedPreferences.Editor editor = mGameSettings.edit();
                            editor.putString(GAME_PREFERENCES_NICKNAME, nicknameText.getText().toString());
                            editor.commit();
                            return true;
                        }
                }
                return false;
            }
        });
    }

    private void initSpinner() {
        int x = loadSpinnerSelection();
        spinner.setSelection(x);
        spinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View itemSelected,
                                           int selectedItemPosition, long selectedId) {
                    SharedPreferences.Editor editor = mGameSettings.edit();
                    editor.putInt(GAME_PREFERENCES_GENDER, spinner.getSelectedItemPosition());
                    editor.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
    }

    private int loadSpinnerSelection() {
        int x = 0;
        if (mGameSettings.contains(GAME_PREFERENCES_GENDER)) {
            x = mGameSettings.getInt(GAME_PREFERENCES_GENDER, 0);
        }
        return x;
    }

    public void clear(View v){
        SharedPreferences.Editor editor = mGameSettings.edit();
        editor.putString(GAME_PREFERENCES_NICKNAME, "");
        editor.putString(GAME_PREFERENCES_EMAIL, "");
        editor.putInt(GAME_PREFERENCES_GENDER, 0);
        editor.remove(GAME_PREFERENCES_DOB);
        editor.remove(GAME_PREFERENCES_PASSWORD);
        editor.commit();

        nicknameText.setText("");
        emailText.setText("");
        initSpinner();
        textPass.setText(R.string.settings_pass_not_set);
        initBirth();
    }

    public void onPickDateButtonClick(View v){
        showDialog(DATE_DIALOG_ID);
    }

    public void onSetPasswordButtonClick(View view) {
        showDialog(PASSWORD_DIALOG_ID);
    }
}
