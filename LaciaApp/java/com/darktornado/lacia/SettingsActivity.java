package com.darktornado.lacia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        Toolbar toolbar = Lacia.creatTitle(this, "Lacia 설정");
        setSupportActionBar(toolbar);
        layout0.addView(toolbar);

        layout.addView(createTitle("웹 브라우저 설정"));
        addWebSettings(layout);
        layout.addView(createTitle("기타 설정"));
        addMiscSettings(layout);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);

        layout0.setBackgroundColor(Color.WHITE);
        layout0.addView(scroll);
        setContentView(layout0);
    }

    private TextView createTitle(String txt){
        int pad = dip2px(5);
        TextView title = new TextView(this);
        title.setText(txt);
        title.setTextSize(17);
        title.setTextColor(Color.BLACK);
        title.setBackgroundColor(Color.parseColor("#BBDEFB"));
        title.setPadding(pad, pad, pad, pad);
        return title;
    }

    private void addWebSettings(LinearLayout layout0){
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        int pad = dip2px(10);
        String[] menuS = {"자바스크립트 허용", "줌 인 사용"};
        final String[] bools = {"useJs", "useZoom"};
        Switch[] sws = new Switch[menuS.length];
        for(int n=0;n<menuS.length;n++){
            if(n>0){
                LineView line = new LineView(this);
                line.setColor(Color.LTGRAY);
                layout.addView(line.mv());
            }
            sws[n] = new Switch(this);
            sws[n].setText(menuS[n]);
            sws[n].setTextSize(20);
            sws[n].setId(n);
            sws[n].setChecked(Lacia.loadSettings(bools[n]));
            sws[n].setPadding(pad, pad, pad, pad);
            sws[n].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton swit, boolean onoff) {
                    Lacia.saveSettings(bools[swit.getId()], onoff);
                }
            });
            layout.addView(sws[n]);
        }
        int pad2 = dip2px(20);
        layout.setPadding(pad2, pad, pad2, pad);
        layout0.addView(layout);
    }

    private void addMiscSettings(LinearLayout layout0){
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        int pad = dip2px(10);
        String[] menuS = {"상시 대기 사용"};
        final String[] bools = {"useWindow"};
        Switch[] sws = new Switch[menuS.length];
        for(int n=0;n<menuS.length;n++){
            if(n>0){
                LineView line = new LineView(this);
                line.setColor(Color.LTGRAY);
                layout.addView(line.mv());
            }
            sws[n] = new Switch(this);
            sws[n].setText(menuS[n]);
            sws[n].setTextSize(20);
            sws[n].setId(n);
            sws[n].setChecked(Lacia.loadSettings(bools[n]));
            sws[n].setPadding(pad, pad, pad, pad);
            sws[n].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton swit, boolean onoff) {
                    int index = swit.getId();
                    Lacia.saveSettings(bools[index], onoff);
                    switch (index) {
                        case 0:
                            Intent intent = new Intent(SettingsActivity.this, MainService.class);
                            if (onoff) {
                                intent.putExtra("chat_data", SettingsActivity.this.getIntent().getStringArrayExtra("chat_data"));
                                startService(intent);
                            } else {
                                stopService(intent);
                            }
                            break;
                    }
                }
            });
            layout.addView(sws[n]);
        }

         String[] menus = {"앱 정보 / 도움말", "깃허브", "제작자 블로그", "라이선스 정보"};
        TextView[] txts = new TextView[menus.length];
        for(int n=0;n<menus.length;n++){
            LineView line = new LineView(this);
            line.setColor(Color.LTGRAY);
            layout.addView(line.mv());
            txts[n] = new TextView(this);
            txts[n].setText(menus[n]);
            txts[n].setTextSize(20);
            txts[n].setTextColor(Color.BLACK);
            txts[n].setId(n);
            txts[n].setPadding(pad, pad, pad, pad);
            txts[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(v.getId()){
                        case 0:
                            showDialog("앱 정보 / 도움말", "앱 이름 : Lacia\n버전 : "+Lacia.VERSION+"\n제작자 : Dark Tornado\n라이선스 : LGPL 3.0\n\n" +
                                    " Nusty의 하위호환인 앱이라고 볼 수 있으며, 음성인식을 지원하는 인공지능 비서 앱이라고 보시면 됩니다.\n" +
                                    " 상시 대기 기능이 활성화된 경우, 화면의 왼쪽 위를 터치하시면 Lacia 메뉴가 열립니다.");
                            break;
                        case 1:
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DarkTornado/Lacia")));
                            break;
                        case 2:
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.naver.com/dt3141592")));
                            break;
                        case 3:
                            startActivity(new Intent(SettingsActivity.this, LicenseActivity.class));
                            break;
                    }
                }
            });
            layout.addView(txts[n]);
        }
        int pad2 = dip2px(20);
        layout.setPadding(pad2, pad, pad2, pad);

        layout0.addView(layout);
    }


    private int dip2px(int dips){
        return (int)Math.ceil(dips*this.getResources().getDisplayMetrics().density);
    }

    public void showDialog(String title, String msg) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(title);
            dialog.setMessage(msg);
            dialog.setNegativeButton("닫기", null);
            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    public void toast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                toast.getView().setBackgroundColor(Color.argb(150, 0, 0, 0));
                int pad = dip2px(5);
                toast.getView().setPadding(pad, pad, pad, pad);
                toast.show();
            }
        });
    }

    private static class LineView{
        private TextView txt;
        private Context ctx;

        public LineView(Context ctx){
            this.ctx = ctx;
            txt = new TextView(ctx);
            txt.setWidth(-1);
            txt.setHeight(dip2px(1));
        }

        public void setColor(int color){
            txt.setBackgroundColor(color);
        }

        public TextView mv(){
            return txt;
        }

        private int dip2px(int dips){
            return (int)Math.ceil(dips*ctx.getResources().getDisplayMetrics().density);
        }

    }

}
