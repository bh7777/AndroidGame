package kr.co.topcredu.chartapplication;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class BarChartActivity extends AppCompatActivity {
    private GraphicalView mChartView;

    private String[] mMonth = new String[] {
            "1월", "2월", "3월", "4월", "5월", "6월",
            "7월", "8월", "9월", "10월", "11월", "12월"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        drawChart();
    }

    private void drawChart() {
        int[] x = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

        int[] income = {210, 250, 270, 300, 280, 350, 370, 380, 270, 210, 280, 300};

        int[] expense = {220, 270, 290, 280, 260, 300, 330, 340, 300, 220, 240, 260};
        //XYSeries 수입
        XYSeries incomeSeries = new XYSeries("수입");
        //XYSeries 지출
        XYSeries expenseSeries = new XYSeries("지출");
        //data를 넣는다
        for (int i=0;i <x.length; i++) {
            incomeSeries.add(i,income[i]);
            expenseSeries.add(i,expense[i]);
        }

        //dataset 생성
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        //dataset에 수입넣기
        dataset.addSeries(incomeSeries);
        dataset.addSeries(expenseSeries);

        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.BLUE);
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setDisplayChartValues(true);
        incomeRenderer.setDisplayChartValuesDistance(10);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();

        expenseRenderer.setColor(Color.RED);

        expenseRenderer.setPointStyle(PointStyle.CIRCLE);

        expenseRenderer.setFillPoints(true);

        expenseRenderer.setLineWidth(2);

        expenseRenderer.setDisplayChartValues(true);


        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);

        multiRenderer.setChartTitle("수입 vs 지출 Chart");

        multiRenderer.setXTitle("2017년");

        multiRenderer.setYTitle("단위 : 만원");


        /***

         * Customizing graphs

         */

        //setting text size of the title

        multiRenderer.setChartTitleTextSize(38);

        multiRenderer.setAxisTitleTextSize(30);

        //setting text size of the graph lable

        multiRenderer.setLabelsTextSize(30);

        //setting zoom buttons visiblity

        multiRenderer.setZoomButtonsVisible(false);

        //setting pan enablity which uses graph to move on both axis

        multiRenderer.setPanEnabled(true, true);

        //setting click false on graph

        multiRenderer.setClickEnabled(true);

        //setting zoom to false on both axis

        multiRenderer.setZoomEnabled(true, true);

        //setting lines to display on y axis

        multiRenderer.setShowGridY(true);

        //setting lines to display on x axis

        multiRenderer.setShowGridX(false);

        //setting legend to fit the screen size

        multiRenderer.setFitLegend(true);

        //setting displaying line on grid

        multiRenderer.setShowGrid(true);

        //setting zoom to false

        multiRenderer.setZoomEnabled(true);

        //setting external zoom functions to false

        multiRenderer.setExternalZoomEnabled(true);

        //setting displaying lines on graph to be formatted(like using graphics)

        multiRenderer.setAntialiasing(true);

        //setting to in scroll to false

        multiRenderer.setInScroll(false);

        //setting to set legend height of the graph

        multiRenderer.setLegendHeight(38);

        //setting x axis label align

        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);

        //setting y axis label to align

        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);

        //setting text style

        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);

        //setting no of values to display in y axis

        multiRenderer.setYLabels(10);

        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.

        // if you use dynamic values then get the max y value and set here

        multiRenderer.setYAxisMax(390);
        multiRenderer.setYAxisMin(180);

        //setting used to move the graph on xaxiz to .5 to the right

        multiRenderer.setXAxisMin(-0.5);

        //setting max values to be display in x axis

        multiRenderer.setXAxisMax(11.5);

        //setting bar size or space between two bars

        multiRenderer.setBarSpacing(0.5);

        //Setting background color of the graph to transparent

        //multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);

        //Setting margin color of the graph to transparent

        multiRenderer.setMarginsColor(Color.TRANSPARENT);

        multiRenderer.setApplyBackgroundColor(true);

        //setting the margin size for the graph in the order top, left, bottom, right

        multiRenderer.setMargins(new int[]{60, 40, 60, 40});

        for (int i = 0; i < x.length; i++) {

            multiRenderer.addXTextLabel(i, mMonth[i]);

        }

        // Adding incomeRenderer and expenseRenderer to multipleRenderer

        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer

        // should be same

        multiRenderer.addSeriesRenderer(incomeRenderer);

        multiRenderer.addSeriesRenderer(expenseRenderer);

        //this part is used to display graph on the xml

        LinearLayout layout = (LinearLayout) findViewById(R.id.chart_bar);

        //remove any views before u paint the chart

        layout.removeAllViews();

        //drawing bar chart

        mChartView = ChartFactory.getBarChartView(this, dataset, multiRenderer, BarChart.Type.DEFAULT);


        layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,

                LinearLayout.LayoutParams.FILL_PARENT));



    }

}
