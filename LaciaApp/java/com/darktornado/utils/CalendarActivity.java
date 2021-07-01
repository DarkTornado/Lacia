package com.darktornado.utils;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.darktornado.lacia.Lacia;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        Toolbar toolbar = Lacia.creatTitle(this, "Lacia 달력");
        setSupportActionBar(toolbar);
        layout0.addView(toolbar);

        CalendarView cal = new CalendarView(this);
        layout.addView(cal);

        int pad = dip2px(10);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);

        layout0.setBackgroundColor(Color.WHITE);
        layout0.addView(scroll);
        setContentView(layout0);
    }

    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

}
