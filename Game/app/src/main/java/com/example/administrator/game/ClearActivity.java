package com.example.administrator.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;

public class ClearActivity extends AppCompatActivity {
    public static final String EXTRA_IS_CLEAR = "com.example.administrator.game.EXTRA.IS_CLEAR";
    public static final String EXTRA_BLOCK_COUNT = "com.example.administrator.game.EXTRA.BLOCK_COUNT";
    public static final String EXTRA_TIME = "com.example.administrator.game.EXTRA.TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config,false);
            }

        } catch (Exception ex) {

        }

        setContentView(R.layout.activity_clear);


        Intent receiveIntent = getIntent();
        if (receiveIntent == null) {
            finish();
        }
        Bundle receiveExxtras =  receiveIntent.getExtras();
        if (receiveExxtras == null) {
            finish();
        }

        boolean isClear = receiveExxtras.getBoolean(EXTRA_IS_CLEAR,false);
        int blockCount = receiveExxtras.getInt(EXTRA_BLOCK_COUNT,0);
        long clearTime = receiveExxtras.getLong(EXTRA_TIME,0);

        TextView textTitle = (TextView) findViewById(R.id.textTitle);
        TextView textBlockCount = (TextView) findViewById(R.id.textBlockCount);
        TextView textClearTime = (TextView) findViewById(R.id.textClearTime);
        TextView textScore = (TextView) findViewById(R.id.textScore);
        final long score = (GameView.BLOCK_COUNT - blockCount) * clearTime;
        //textScore.setText(getString(R.string.score,score));
        textScore.setText("현재점수 : " + String.format("%,d", score));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long highScore = sharedPreferences.getLong("high_score",0);
        if (highScore < score) {
            highScore = score;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("high_score",highScore);
            editor.commit();

        }
        TextView textHighScore = (TextView) findViewById(R.id.textHighScore);
        //textHighScore.setText(getString(R.string.high_score,highScore));
        textHighScore.setText("최고점수 : " + String.format("%,d", highScore));

        Button gameStart = (Button)findViewById(R.id.buttonGameStart);
        if (isClear) {
            textTitle.setText(R.string.clear);
        } else {
            textTitle.setText(R.string.game_over);
        }
        textBlockCount.setText(getString(R.string.block_count, String.valueOf(blockCount)));
        textClearTime.setText(getString(R.string.time, clearTime / 1000, clearTime % 1000));

        //Share처리
        Button buttonShare = (Button) findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "벽돌깨기 점수 : "+ String.format("%,d", score));
                startActivity(intent);
            }

        });

        gameStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClearActivity.this, GameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clear, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings1) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
