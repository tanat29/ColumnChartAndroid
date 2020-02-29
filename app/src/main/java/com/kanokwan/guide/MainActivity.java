package com.kanokwan.guide;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AnyChartView anyChartView;
    Cartesian cartesian;
    List<DataEntry> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));
        cartesian = AnyChart.column();
        data = new ArrayList<>();

        Load_Data_Graph();
    }

    private void Load_Data_Graph() {
        Ion.with(this) // ดึงข้อมูลจาก url มา
                .load("http://testingmyproject.com/coffee/testingjson.php")
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> result) {

                        try {
                            String aa = result.getResult();

                            if (aa.equals("[]")) {
                                // ถ้าไม่มีข้อมูลให้แสดง Toast
                                Toast.makeText(MainActivity.this, "ไม่มีข้อมูล", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONArray jsonArray = new JSONArray(aa);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                data.add(new ValueDataEntry(obj.getString("name").trim(), obj.getInt("caffeine")));
                            }

                            Column column = cartesian.column(data);

                            column.tooltip()
                                    .titleFormat("{%X}")
                                    .position(Position.CENTER_BOTTOM)
                                    .anchor(Anchor.CENTER_BOTTOM)
                                    .offsetX(0d)
                                    .offsetY(5d)
                                    .format("{%Value}{groupsSeparator: }");

                            cartesian.animation(true);
                            cartesian.title("กราฟปริมาณคาเฟอีนที่ได้รับในแต่ล่ะวัน");
                            cartesian.yScale().minimum(0d);
                            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: } mg");
                            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                            cartesian.interactivity().hoverMode(HoverMode.BY_X);
                            cartesian.xAxis(0).title("วันที่");
                            cartesian.yAxis(0).title("คาเฟอีน");

                            anyChartView.setChart(cartesian);
                        } catch (Exception t) {
                            Toast.makeText(MainActivity.this, "ไม่สามารถโหลดข้อมูลได้", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }
}