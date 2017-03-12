package kr.co.topcredu.chartapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    public static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //파이그래프
        Button btnPieChart = (Button) findViewById(R.id.btnPieChart);
        btnPieChart.setOnClickListener(this);

        //라인그래프
        Button btnLineChart = (Button) findViewById(R.id.btnLineChart);
        btnLineChart.setOnClickListener(this);

        //막대그래프
        Button btnBarChart = (Button) findViewById(R.id.btnBarChart);
        btnBarChart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //파이그래프
            case R.id.btnPieChart :
                Toast.makeText(MainActivity.this, "파이 차트", Toast.LENGTH_SHORT);;
                Log.i(LOG_TAG,"파이차트 시작...");
                startActivity(new Intent(this,PieChartActivity.class));
                break;
            case R.id.btnLineChart :
                Toast.makeText(MainActivity.this, "라인 차트", Toast.LENGTH_SHORT);;
                Log.i(LOG_TAG,"라인차트 시작...");
                startActivity(new Intent(this,LineChartActivity.class));
                break;
            case R.id.btnBarChart :
                Toast.makeText(MainActivity.this, "바 차트", Toast.LENGTH_SHORT);;
                Log.i(LOG_TAG,"바차트 시작...");
                startActivity(new Intent(this,BarChartActivity.class));
                break;
        }

    }
}
