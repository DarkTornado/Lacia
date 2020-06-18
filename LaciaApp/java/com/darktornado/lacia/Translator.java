package com.darktornado.lacia;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;

public class Translator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        Toolbar toolbar = Lacia.creatTitle(this, "Lacia 번역기");
        setSupportActionBar(toolbar);
        layout0.addView(toolbar);

        StrictMode.enableDefaults();

        String[] names = {"한국어", "영어", "일본어", "중국어 (간체)", "중국어 (번체)", "러시아어", "라틴어"};
        final String[] codes = {"ko", "en", "ja", "zh-CN", "zh-TW", "ru", "la"};

        final int[] langs = {0, 0};

        Spinner spin1 = new Spinner(this);
        final ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        spin1.setAdapter(adapter1);
        spin1.setSelection(langs[0]);
        spin1.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                langs[0] = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        layout.addView(spin1);
        final EditText txt1 = new EditText(this);
        txt1.setHint("번역할 내용을 입력해주세요...");
        txt1.setTextColor(Color.BLACK);
        txt1.setHintTextColor(Color.GRAY);
        layout.addView(txt1);
        layout.addView(getBlank());

        Spinner spin2 = new Spinner(this);
        final ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        spin2.setAdapter(adapter2);
        spin2.setSelection(langs[1]);
        spin2.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                langs[1] = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        layout.addView(spin2);
        final EditText txt2 = new EditText(this);
        txt2.setHint("번역된 내용이 출력되는 곳...");
        txt2.setTextColor(Color.BLACK);
        txt2.setHintTextColor(Color.GRAY);
        layout.addView(txt2);
        layout.addView(getBlank());

        LinearLayout lay2 = new LinearLayout(this);
        lay2.setWeightSum(2);
        Button trans = new Button(this);
        trans.setText("번역");
        trans.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = codes[langs[0]];
                String to = codes[langs[1]];
                translate(from, to, txt1.getText().toString(), txt2);
            }
        });
        lay2.addView(trans);
        Button copy = new Button(this);
        copy.setText("복사");
        copy.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("label", txt2.getText().toString()));
                toast("번역 결과가 복사되었습니다.");
            }
        });
        lay2.addView(copy);
        layout.addView(lay2);

        TextView maker = new TextView(this);
        maker.setText("\n© " + Lacia.COPYRIGHT_YEAR + " Dark Tornado, All rights reserved.\n");
        maker.setTextSize(13);
        maker.setTextColor(Color.BLACK);
        maker.setGravity(Gravity.CENTER);
        layout.addView(maker);

        int pad = dip2px(20);
        layout.setPadding(pad, pad, pad, pad);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);

        layout0.setBackgroundColor(Color.WHITE);
        layout0.addView(scroll);
        setContentView(layout0);
    }

    private TextView getBlank() {
        TextView txt = new TextView(this);
        txt.setText(" ");
        txt.setTextSize(16);
        return txt;
    }

    private void translate(String from, String to, String value, EditText txt) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://translate.googleapis.com/translate_a/single?client=gtx&sl=" + from + "&tl=" + to + "&dt=t&q=" + URLEncoder.encode(value, "UTF-8") + "&ie=UTF-8&oe=UTF-8");
            HttpResponse res = client.execute(get);
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            res.getEntity().writeTo(content);
            content.close();
            String result = content.toString().split("\\[\\[\"")[1].split("\",\"")[0];
            txt.setText(result);
            toast("번역하였습니다.");
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private int dip2px(int dips) {
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
