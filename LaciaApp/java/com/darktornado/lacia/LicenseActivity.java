package com.darktornado.lacia;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        Toolbar toolbar = Lacia.creatTitle(this, "라이선스 정보");
        setSupportActionBar(toolbar);
        layout0.addView(toolbar);


        final String value = loadLicense();
        TextView txt = new TextView(this);
        txt.setText(value);
        txt.setTextSize(17);
        txt.setTextColor(Color.BLACK);
        layout.addView(txt);

        int pad = dip2px(20);
        layout.setPadding(pad, dip2px(30), pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        layout0.addView(scroll);
        layout0.setBackgroundColor(Color.WHITE);
        setContentView(layout0);
    }

    private String loadLicense(){
        try {
            return Lacia.readAsset(this, "license.txt");
        }catch (Exception e){
            toast(e.toString());
            return "라이선스 정보 불러오기 실패";
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

    private int dip2px(int dips){
        return (int)Math.ceil(dips*this.getResources().getDisplayMetrics().density);
    }

}
