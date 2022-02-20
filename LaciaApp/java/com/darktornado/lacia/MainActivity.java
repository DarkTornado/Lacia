package com.darktornado.lacia;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.darktornado.utils.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kotlin.Pair;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer = null;
    TextToSpeech tts;
    LinearLayout chats = null;
    String[] chatData = null;
    boolean blockDrawer = false;
    String[][] appList = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (blockDrawer) {
                toast("Lacia 사용을 위해서는 권한 허용을 먼저 진행해주세요.");
                return super.onOptionsItemSelected(item);
            }
            if (drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            } else {
                drawer.openDrawer(Gravity.LEFT);
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || checkNoPermission()) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(1);
            Toolbar toolbar = new Toolbar(this);
            toolbar.setTitle("Lacia 권한 허용 요청");
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setBackgroundColor(Lacia.getColor());
            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -2);
            margin.setMargins(0, 0, 0, dip2px(8));
            toolbar.setLayoutParams(margin);
            ViewCompat.setElevation(toolbar, dip2px(5));
            setSupportActionBar(toolbar);
            layout.addView(toolbar);
            ScrollView scroll = showPermission();
            if (scroll == null) {
                toast("오류 발생");
            } else {
                layout.addView(scroll);
                setContentView(layout);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(Lacia.getIcon());
            }
            blockDrawer = true;
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Lacia");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Lacia.getColor(190));
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -2);
        margin.setMargins(0, 0, 0, dip2px(8));
        toolbar.setLayoutParams(margin);
        ViewCompat.setElevation(toolbar, dip2px(5));
        setSupportActionBar(toolbar);
        layout0.addView(toolbar);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREAN);
            }
        });

        chats = layout;

        int pad = dip2px(20);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);

        scroll.setPadding(0, 0, 0, dip2px(67));
        scroll.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));

        FrameLayout layoutf = new FrameLayout(this);
        layoutf.addView(scroll);
        layoutf.addView(makeInputButton());
        layoutf.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));

        layout0.addView(layoutf);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.getAssets().open("background.jpg"));
            layout0.setBackgroundDrawable(new BitmapDrawable(bitmap));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(Lacia.getIcon());
            drawer = new DrawerLayout(this);
            drawer.addView(layout0);
            drawer.addView(createDrawerLayout());
            setContentView(drawer);
        } catch (Exception e) {
            toast(e.toString());
            setContentView(layout0);
        }
        toast("채팅 데이터 로드 시작");
        new Thread(new Runnable() {
            @Override
            public void run() {
                chatData = Lacia.readAsset(MainActivity.this, "chatData.txt").split("\n");
                toast("채팅 데이터 로드 완료");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[][] apps = Lacia.getAllApps(getApplicationContext());
                    appList = new String[apps.length][];
                    for (int n = 0; n < apps.length; n++) {
                        appList[n] = new String[2];
                        appList[n][0] = apps[n][0];
                        appList[n][1] = apps[n][1];
                    }
                } catch (Exception e) {
                    toast(e.toString());
                }
            }
        }).start();
        Lacia.initSettings();
        if (getIntent().getBooleanExtra("input_start", false)) {
            inputVoice();
        }
    }

    private LinearLayout createDrawerLayout() {
        try {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(1);
            int pad = dip2px(15);
            TextView vers = new TextView(this);
            vers.setText("vers. " + Lacia.VERSION);
            vers.setTextSize(15);
            vers.setTextColor(Color.WHITE);
            vers.setBackgroundColor(Lacia.getColor());
            vers.setGravity(Gravity.RIGHT);
            vers.setPadding(pad, dip2px(5), pad, dip2px(5));
            layout.addView(vers);
            TextView title = new TextView(this);
            title.setText("Lacia");
            title.setTextSize(24);
            title.setTextColor(Color.WHITE);
            title.setBackgroundColor(Lacia.getColor());
            title.setPadding(dip2px(21), 0, pad, dip2px(6));
            layout.addView(title);
            TextView maker = new TextView(this);
            maker.setText("ⓒ " + Lacia.COPYRIGHT_YEAR + " Dark Tornado");
            maker.setTextSize(12);
            maker.setTextColor(Color.WHITE);
            maker.setBackgroundColor(Lacia.getColor());
            maker.setPadding(dip2px(21), 0, pad, dip2px(15));
            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -2);
            margin.setMargins(0, 0, 0, dip2px(10));
            maker.setLayoutParams(margin);
            layout.addView(maker);
            String[] menus = {"대화 내용 삭제", "명령어 목록", "웹 브라우저", "음악 플레이어", "기타 기능", "제작자 블로그", "도움말", "환경 설정"};
            ListView list = new ListView(this);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, menus);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    switch (pos) {
                        case 0:
                            chats.removeAllViews();
                            break;
                        case 1:
                            showDialog("명령어 목록", "- 음악\n- 음악 정지\n- 검색 [검색어]\n- 길찾기\n- 노선도\n- 시간\n- 달력\n- 날씨\n- 종료\n- 와이파이\n- 와이파이 켜\n- 와이파이 꺼\n- 번역\n- 번역기");
                            break;
                        case 2:
                            startActivity(new Intent(getApplicationContext(), WebActivity.class));
                            break;
                        case 3:
                            startActivity(new Intent(getApplicationContext(), MusicActivity.class));
                            break;
                        case 4:
                            miscDialog();
                            break;
                        case 5:
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.naver.com/dt3141592")));
                            break;
                        case 6:
                            showDialog("앱 정보 / 도움말", "앱 이름 : Lacia\n버전 : " + Lacia.VERSION + "\n제작자 : Dark Tornado\n라이선스 : LGPL 3.0\n\n" +
                                    " Nusty의 하위호환인 앱이라고 볼 수 있으며, 음성인식을 지원하는 인공지능 비서 앱이라고 보시면 됩니다.\n" +
                                    " 상시 대기 기능이 활성화된 경우, 화면의 왼쪽 위를 터치하시면 Lacia 메뉴가 열립니다.");
                            break;
                        case 7:
                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            intent.putExtra("chat_data", chatData);
                            startActivity(intent);
                            break;
                    }
                }
            });
            layout.addView(list);
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(-1, -1);
            params.gravity = Gravity.LEFT;
            layout.setLayoutParams(params);
            layout.setBackgroundColor(Color.WHITE);
            pad = dip2px(5);
            list.setPadding(pad, pad, pad, pad);
            return layout;
        } catch (Exception e) {
            toast(e.toString());
        }
        return null;
    }

    public void inputVoice() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            final SpeechRecognizer stt = SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    toast("Input Ready");
                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                }

                @Override
                public void onEndOfSpeech() {
                    toast("Input End");
                }

                @Override
                public void onError(int error) {
                    try {
                        toast("Error Code : " + error);
                        stt.destroy();
                    } catch (Exception e) {
                        toast("OnError\n" + e.toString());
                    }
                }

                @Override
                public void onResults(Bundle results) {
                    final ArrayList<String> result = (ArrayList<String>) results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (result.get(0).contains(("러스티"))) {
                        result.add(0, result.get(0).replace("러스티", "너스티"));
                    }
                    if (result.get(0).contains(("러스트"))) {
                        result.add(0, result.get(0).replace("러스트", "너스티"));
                    }
                    if (result.get(0).contains(("비너스")) && Math.floor(Math.random() * 2) == 0) {
                        result.add(0, result.get(0).replace("비너스", "티너스"));
                    }
                    final String que = (String) result.get(0);
                    createChatBubble(que, true);
                    stt.destroy();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            showAnswer(que);
                        }
                    }, 500);
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            stt.startListening(intent);
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    public void inputChat() {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(1);
            TextView txt1 = new TextView(this);
            txt1.setText("채팅 : ");
            txt1.setTextSize(18);
            txt1.setTextColor(Color.BLACK);
            layout.addView(txt1);
            final EditText txt2 = new EditText(this);
            txt2.setHint("채팅을 입력하세요...");
            txt2.setSingleLine(true);
            layout.addView(txt2);
            int pad = dip2px(10);
            layout.setPadding(pad, pad, pad, pad);
            ScrollView scroll = new ScrollView(this);
            scroll.addView(layout);
            dialog.setView(scroll);
            dialog.setTitle("채팅 입력");
            dialog.setNegativeButton("취소", null);
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    final String input = txt2.getText().toString();
                    createChatBubble(input, true);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            showAnswer(input);
                        }
                    }, 1500);
                }
            });
            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    public void showAnswer(String msg) {
        try {
            if (msg.equals("길 찾기")) msg = "길찾기";
            String cmd = msg.split(" ")[0];
            final String data = msg.replaceFirst(cmd + " ", "");
            if (msg.contains("실행") || msg.contains("켜")) {
                try {
                    for (String[] app : appList) {
                        if (msg.replace(" ", "").contains(app[0].replace(" ", ""))) {
                            PackageManager pm = getPackageManager();
                            startActivity(pm.getLaunchIntentForPackage(app[1]));
                            say(app[0] + " 실행합니다.");
                            return;
                        }
                    }
                } catch (Exception e) {
                    toast(e.toString());
                }
            }
            Calendar day = Calendar.getInstance();
            switch (cmd) {
                case "음악":
                case "노래":
                    if (data.equals("정지")) {
                        stopService(new Intent(this, MusicService.class));
                        say("음악을 정지합니다.");
                    } else {
                        startService(new Intent(this, MusicService.class));
                        say("음악을 재생합니다.");
                    }
                    break;
                case "검색":
                    say(data + "에 대한 검색결과입니다.");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            showWebView(data);
                        }
                    }, 1000);
                    break;
                case "길찾기":
                    say("길찾기를 실행합니다.");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            showWebView("길찾기");
                        }
                    }, 1000);
                    break;
                case "노선도":
                    say("노선도를 띄웁니다.");
                    startActivity(new Intent(this, MetroActivity.class));
                    break;
                case "시간":
                    say("현재 시각은 " + day.get(Calendar.HOUR) + "시 " + day.get(Calendar.MINUTE) + "분 " + day.get(Calendar.SECOND) + "초입니다.");
                    break;
                case "달력":
                    showCalendar();
                    say("달력을 띄웁니다.");
                    break;
                case "날씨":
                    if (msg.equals("날씨")) {
                        say("전국 날씨입니다.");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                showWebView("전국 날씨");
                            }
                        }, 500);
                    } else {
                        say(data + "의 날씨 정보입니다.");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                WeatherParser wp = new WeatherParser(MainActivity.this, data);
                                Pair<String, String> result = wp.parse();
                                if (result == null) {
                                    toast("해당 지역을 찾을 수 없습니다.");
                                } else {
                                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                                    intent.putExtra("pos", result.getFirst());
                                    intent.putExtra("data", result.getSecond());
                                    startActivity(intent);
                                }
                            }
                        }).start();
                    }
                    break;
                case "와이파이":
                    WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    boolean wifiOn = wm.isWifiEnabled();
                    if (data.equals("꺼")) {
                        if (wifiOn) {
                            wm.setWifiEnabled(false);
                            say("와이파이를 껐습니다.");
                        } else {
                            say("이미 와이파이가 꺼진 상태입니다.");
                        }
                    } else {
                        if (!wifiOn) {
                            wm.setWifiEnabled(true);
                            say("와이파이를 켰습니다.");
                        } else {
                            say("이미 와이파이가 켜진 상태입니다.");
                        }
                    }
                    break;
                case "번역":
                case "번역기":
                    say("번역기를 실행합니다.");
                    startActivity(new Intent(MainActivity.this, Translator.class));
                    break;
                case "불":
                case "전등":
                case "손전등":
                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        if (cm == null) {
                            toast("기기에 카메라가 없거나, 카메라를 찾을 수 없습니다.");
                        } else {
                            String camera = cm.getCameraIdList()[0];
                            if (data.equals("꺼")) {
                                cm.setTorchMode(camera, false);
                                say("손전등을 껐습니다.");
                            } else {
                                cm.setTorchMode(camera, true);
                                say("손전등을 켰습니다.");
                            }
                        }
                    } else {
                        say("롤리팝 미만에서는 작동하지 않습니다.");
                    }
                    break;
                case "버스":
                    say("버스 운행정보를 불러옵니다.");
                    Intent intent = new Intent(MainActivity.this, BusActivity.class);
                    intent.putExtra("input", data);
                    startActivity(intent);
                    break;
                case "잘가":
                case "종료":
                    say("그럼 난 갈께.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                    break;
                default:
                    String reply = Lacia.getReply(chatData, msg);
                    if (reply == null) say("무슨 말인지 잘 모르겠어요.");
                    else say(reply);
                    break;
            }

        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void showWebView(String data) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }


    private void say(final String chat) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createChatBubble(chat, false);
                tts.speak(chat, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else {
            finish();
        }
    }


    public void showCalendar() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
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


    public void createChatBubble(String chat, boolean isRight) {
        LinearLayout lay = new LinearLayout(this);
        lay.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        int mar = dip2px(5);
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-2, -2);

        TextView txt = new TextView(this);
        txt.setTextColor(Color.BLACK);

        if (isRight) {
            lay.setGravity(Gravity.RIGHT);
            margin.setMargins(dip2px(30), mar, mar, mar);
            txt.setBackgroundColor(Color.parseColor("#BBDEFB"));
        } else {
            margin.setMargins(mar, mar, dip2px(30), mar);
            txt.setBackgroundColor(Color.WHITE);
        }

        ViewCompat.setElevation(txt, dip2px(3));
        txt.setText(chat);
        txt.setLayoutParams(margin);
        txt.setPadding(mar, mar, mar, mar);
        txt.setTextSize(19);
        lay.addView(txt);
        ScaleAnimation ani = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ani.setDuration(500);
        txt.setAnimation(ani);

        chats.addView(lay);

    }

    private TextView makeInputButton() {
        TextView input = new TextView(this);
        try {
            int size = dip2px(50);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER;
            input.setLayoutParams(params);
            input.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputVoice();
                }
            });
            input.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    inputChat();
                    return true;
                }
            });
            input.setBackgroundResource(R.mipmap.voice_input);
        } catch (Exception e) {
            toast(e.toString());
        }
        return input;
    }

    private void miscDialog() {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("기타 기능");
            String[] menus = {"노선도", "달력", "번역기"};
            dialog.setItems(menus, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface m, int w) {
                    switch (w) {
                        case 0:
                            startActivity(new Intent(MainActivity.this, MetroActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(MainActivity.this, Translator.class));
                            break;
                    }
                }
            });
            dialog.setNegativeButton("취소", null);
            dialog.show();
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private boolean checkNoPermission() {
        if (Build.VERSION.SDK_INT < 23) return false;
        if (!Settings.canDrawOverlays(this)) return true;
        return false;
    }

    private ScrollView showPermission() {
        try {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(1);
            TextView txt1 = new TextView(this);
            txt1.setText(Html.fromHtml("&nbsp;<b>내장메모리 접근 권한</b>은 앱 설정 등을 기기에 저장할 때 필요한 권한입니다.<br><br>&nbsp;<b>음성 녹음 권한</b>은 인터넷 권한과 함께 음성 인식에 사용됩니다. 음성 인식의 작동 방식이 '마이크로 목소리 녹음 => 구글에게 전송 => 구글로부터 음성 인식 결과 수신'이라서 필요한 권한입니다.<br><br>&nbsp;화면 오버레이 어쩌고가 뜨는 경우는 현재 실행중인 앱들 중 하나가 이 화면을 건드는(?) 중이라서 그렇습니다. 안건들거나 못건들도록 하면 안뜰겁니다. 이미 권한이 허용되어 있는 경우에는 '권한 허용하기' 버튼을 눌렀을 때 아무것도 뜨지 않습니다.<br>"));
            txt1.setTextSize(18);
            txt1.setTextColor(Color.BLACK);
            layout.addView(txt1);
            Button btn1 = new Button(this);
            btn1.setText("권한 허용하기");
            btn1.setTransformationMethod(null);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 3);
                }
            });
            layout.addView(btn1);

            if (Build.VERSION.SDK_INT >= 23) {
                TextView txt2 = new TextView(this);
                txt2.setText(Html.fromHtml("<br>&nbsp;<b>다른 앱 위에 그리기</b> 또는 <b>시스템 알림 표시</b>라고 불리는 권한은 플로팅 창을 띄울 때 필요한 권한입니다. 안드로이드 8.0 이하의 경우, Play 스토어에서 받은 경우라면 자동으로 허용되어있을겁니다.<br>"));
                txt2.setTextSize(18);
                txt2.setTextColor(Color.BLACK);
                layout.addView(txt2);
                Button btn2 = new Button(this);
                btn2.setText("권한 허용하기");
                btn2.setTransformationMethod(null);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri);
                        startActivityForResult(intent, 5469);
                    }
                });
                layout.addView(btn2);
            }

            TextView txt4 = new TextView(this);
            txt4.setText("\n 위에서 언급된 권한들을 허용해주셔야, Nusty가 정상적으로 작동합니다. 권한 악용(?)은 하지 않는데, '위험한 권한' 또는 '매우 위험한 권한'으로 분류되는 것들이라, 몇몇 백신들은 Lacia를 바이러스로 분류할 가능성이 있습니다. 정 의심되신다면, 디컴파일해보세요?\n\n 권한 허용을 하신 뒤에는 Lacia를 다시 시작해주세요.\n");
            txt4.setTextSize(18);
            txt4.setTextColor(Color.BLACK);
            layout.addView(txt4);

            Button restart = new Button(this);
            restart.setText("Lacia 재시작");
            restart.setTransformationMethod(null);
            restart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    toast("앱이 재시작됩니다...");
                }
            });
            layout.addView(restart);
            TextView maker = new TextView(this);
            maker.setText("\nⓒ " + Lacia.COPYRIGHT_YEAR + " Dark Tornado, All rights reserved.\n");
            maker.setTextSize(13);
            maker.setTextColor(Color.BLACK);
            maker.setGravity(Gravity.CENTER);
            layout.addView(maker);
            int pad = dip2px(15);
            layout.setPadding(pad, pad, pad, pad);
            ScrollView scroll = new ScrollView(this);
            scroll.addView(layout);
            return scroll;
        } catch (Exception e) {
            toast(e.toString());
        }
        return null;
    }

}
