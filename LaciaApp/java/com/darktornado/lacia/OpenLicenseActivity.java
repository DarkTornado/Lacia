package com.darktornado.lacia;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class OpenLicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        Toolbar toolbar = Lacia.creatTitle(this, "오픈 소스 라이선스 정보");
        setSupportActionBar(toolbar);
        layout0.addView(toolbar);

        loadLicenseInfo(layout, "Jsoup", "Jsoup", "MIT License", "Jonathan Hedley", true);

        int pad = dip2px(20);
        layout.setPadding(pad, dip2px(10), pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        layout0.addView(scroll);
        layout0.setBackgroundColor(Color.WHITE);
        setContentView(layout0);
    }

    private void loadLicenseInfo(LinearLayout layout, String name, String fileName, final String license, String dev, boolean tf) {
        int pad = dip2px(10);
        TextView title = new TextView(this);
        if(tf) title.setText(Html.fromHtml("<b>"+name+"<b>"));
        else title.setText(Html.fromHtml("<br><b>"+name+"<b>"));
        title.setTextSize(24);
        title.setTextColor(Color.BLACK);
        title.setPadding(pad, 0, pad, dip2px(1));
        layout.addView(title);
        TextView subtitle = new TextView(this);
        subtitle.setText("  by " + dev + ", " + license);
        subtitle.setTextSize(20);
        subtitle.setTextColor(Color.BLACK);
        subtitle.setPadding(pad, 0, pad, pad);
        layout.addView(subtitle);

        final String value = loadLicense(fileName);
        TextView txt = new TextView(this);
        if(value.length()>1500){
            txt.setText(Html.fromHtml(value.substring(0, 1500).replace("\n", "<br>")+"...<font color='#757575'><b>[Show All]</b></font>"));
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(license, value);
                }
            });
        }
        else{
            txt.setText(value);
        }
        txt.setTextSize(17);
        txt.setTextColor(Color.BLACK);
        txt.setPadding(pad, pad, pad, pad);
        txt.setBackgroundColor(Color.parseColor("#E3F2FD"));
        layout.addView(txt);
    }

    private String loadLicense(String name){
        try {
            return Lacia.readAsset(this, "license/" + name + ".txt");
        }catch (Exception e){
            toast(e.toString());
            return "라이선스 정보 불러오기 실패";
        }
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

    private int dip2px(int dips){
        return (int)Math.ceil(dips*this.getResources().getDisplayMetrics().density);
    }
}
