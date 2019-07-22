package com.darktornado.lacia;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MetroActivity extends AppCompatActivity {

    WebView web;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "수도권 광역철도").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 1, 0, "부산 도시철도").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 2, 0, "대전 도시철도").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 3, 0, "대구 도시철도").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 4, 0, "광주 도시철도").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            String[] names = {"seoul", "busan", "daejeon", "daegu", "gwangju"};
            int id = item.getItemId();
            web.loadUrl("file:///android_asset/metro_map/"+names[id]+".png");
        } catch (Exception e) {
            toast(e.toString());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        Toolbar toolbar = Lacia.creatTitle(this, "Lacia 전철 노선도");
        setSupportActionBar(toolbar);
        layout0.addView(toolbar);

        web = new WebView(this);
        WebSettings webSet = web.getSettings();
        webSet.setBuiltInZoomControls(true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient());
        web.loadUrl("file:///android_asset/metro_map/seoul.png");
        layout.addView(web);

        int pad = dip2px(10);
        layout.setPadding(pad, pad, pad, pad);
        layout0.setBackgroundColor(Color.WHITE);
        layout0.addView(layout);

        setContentView(layout0);
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

    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

}
