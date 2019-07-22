package com.darktornado.lacia;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class WebActivity extends AppCompatActivity {

    WebView web;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "뒤로").setIcon(R.drawable.web_back).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 1, 0, "앞으로").setIcon(R.drawable.web_forard).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 0, "새로 고침").setIcon(R.drawable.web_reload).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 3, 0, "URL 설정").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 4, 0, "종료").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch(item.getItemId()){
                case 0:
                    if(web.canGoBack()) web.goBack();
                    break;
                case 1:
                    if(web.canGoForward()) web.goForward();
                    break;
                case 2:
                    web.reload();
                    break;
                case 3:
                    inputUrl();
                    break;
                case 4:
                    finish();
                    break;
            }
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
        Toolbar toolbar = Lacia.creatTitle(this, "Lacia 브라우저");
        setSupportActionBar(toolbar);
        layout0.addView(toolbar);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        web = new WebView(this);
        WebSettings webSet = web.getSettings();
        webSet.setJavaScriptEnabled(Lacia.loadSettings("useJs"));
        webSet.setBuiltInZoomControls(Lacia.loadSettings("useZoom"));
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient());
        if (data == null) web.loadUrl("https://www.naver.com");
        else web.loadUrl("https://m.search.naver.com/search.naver?query=" + data);
        layout.addView(web);

        layout0.addView(layout);
        setContentView(layout0);
    }

    public void inputUrl() {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(1);
            TextView txt1 = new TextView(this);
            txt1.setText("URL : ");
            txt1.setTextSize(18);
            txt1.setTextColor(Color.BLACK);
            layout.addView(txt1);
            final EditText txt2 = new EditText(this);
            txt2.setHint("URL을 입력하세요...");
            txt2.setText(web.getUrl());
            txt2.setSingleLine(true);
            layout.addView(txt2);
            int pad = dip2px(10);
            layout.setPadding(pad, pad, pad, pad);
            ScrollView scroll = new ScrollView(this);
            scroll.addView(layout);
            dialog.setView(scroll);
            dialog.setTitle("URL 입력");
            dialog.setNegativeButton("취소", null);
            dialog.setNeutralButton("URL 복사", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText("label", txt2.getText().toString()));
                    toast("복사되었습니다.");
                }
            });
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    String url = txt2.getText().toString();
                    if (url.equals("")) {
                        toast("URL이 입력되지 않았습니다.");
                        inputUrl();
                    } else {
                        web.loadUrl(url);
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else finish();
    }

    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
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
