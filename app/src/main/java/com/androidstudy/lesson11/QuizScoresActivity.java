package com.androidstudy.lesson11;

import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class QuizScoresActivity extends QuizActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scores);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));

        loadScores();
	}

    private void loadScores() {
        TabHost host = (TabHost) findViewById(R.id.TabHost1);
        host.setup();

        newTabSpec(host, R.string.all_scores, android.R.drawable.star_on, R.id.Scroll_All);
        newTabSpec(host, R.string.friends_scores, android.R.drawable.star_on, R.id.Scroll_Friends);
        host.setCurrentTabByTag(getResources().getString(R.string.all_scores));

        TableLayout allTable = (TableLayout) findViewById(R.id.TL_All);
        TableLayout friendsTable = (TableLayout) findViewById(R.id.TL_Friends);
        addHeaderOnTableLayout(allTable);
        addHeaderOnTableLayout(friendsTable);

        try {
            loadXML(R.xml.allscores, allTable);
            loadXML(R.xml.friendscores, friendsTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addHeaderOnTableLayout(TableLayout table) {
        insertRowToTable(table,
                getResources().getString(R.string.rank),
                getResources().getString(R.string.username),
                getResources().getString(R.string.score),
                1.5f);
    }

    private void newTabSpec(TabHost host, int scores, int icon, int scroll) {
        TabHost.TabSpec scoresTab = host.newTabSpec(getResources().getString(scores));
        scoresTab.setIndicator(getResources().getString(scores),
                getResources().getDrawable(icon));
        scoresTab.setContent(scroll);
        host.addTab(scoresTab);
    }

    private void loadXML(int xmlFile, TableLayout scoreTable)
            throws IOException, XmlPullParserException {

        XmlResourceParser mockScores = getResources().getXml(xmlFile);
        int eventType = -1;
        boolean bFoundScores = false;

        // Find Score records from XML
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            if (eventType == XmlResourceParser.START_TAG) {
            // Get the name of the tag (eg scores or score)
                String strName = mockScores.getName();
                if (strName.equals("score")) {
                    bFoundScores = true;
                    String scoreValue = mockScores.getAttributeValue(null, "score");
                    String scoreRank = mockScores.getAttributeValue(null, "rank");
                    String scoreUserName = mockScores.getAttributeValue(null, "username");
                    insertRowToTable(scoreTable, scoreRank, scoreUserName, scoreValue, 1);
                }
            }
            eventType = mockScores.next();
        }
        if (!bFoundScores) {
            final TableRow newRow = new TableRow(this);
            TextView noResults = new TextView(this);
            noResults.setText(getResources().getString(R.string.no_scores));
            newRow.addView(noResults);
            scoreTable.addView(newRow);
        }
    }

    private void insertRowToTable(TableLayout table, String rank, String user,
                                  String score, float textSize){
        final TableRow thisRow = new TableRow(this);
        addColumnToThisRow(thisRow, rank, textSize);
        addColumnToThisRow(thisRow, user, textSize);
        addColumnToThisRow(thisRow, score, textSize);
        table.addView(thisRow);
    }

    private void addColumnToThisRow(TableRow row, String text, float factor) {
        TextView textView = new TextView(this);
        textView.setTextSize(getResources().getDimension(R.dimen.help_text_size)*factor);
        textView.setTextColor(getResources().getColor(R.color.logo_color));
        textView.setText(text);
        row.addView(textView);
    }

}
