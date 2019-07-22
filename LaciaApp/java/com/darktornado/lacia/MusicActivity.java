package com.darktornado.lacia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

public class MusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        Toolbar toolbar = Lacia.creatTitle(this, "Lacia 뮤직 플레이어");
        setSupportActionBar(toolbar);
        layout0.addView(toolbar);

        Button stop = new Button(this);
        stop.setText("음악 정지");
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MusicActivity.this, MusicService.class));
            }
        });
        layout.addView(stop);

        final String[] music = Lacia.getAllAudio(this);
        Arrays.sort(music);
        String[] names = new String[music.length];
        for(int n=0;n<music.length;n++){
            names[n] = new File(music[n]).getName();
        }
        ListView list = new ListView(this);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent intent = new Intent(getApplicationContext(), MusicService.class);
                intent.putExtra("music", music[pos]);
                startService(intent);
            }
        });
        int pad = dip2px(5);
        list.setPadding(pad, dip2px(15), pad, pad);
        layout.addView(list);
        pad = dip2px(20);
        layout.setPadding(pad, pad, pad, pad);

        layout0.addView(layout);

        setContentView(layout0);

    }

    public int dip2px(int dips) {
        return (int) Math.ceil((double)(((float) dips) * getResources().getDisplayMetrics().density));
    }

    public void toast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                toast.getView().setBackgroundColor(Lacia.getColor(190));
                int pad = dip2px(5);
                toast.getView().setPadding(pad, pad, pad, pad);
                toast.show();
            }
        });
    }
}
