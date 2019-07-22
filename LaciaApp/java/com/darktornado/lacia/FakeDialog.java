package com.darktornado.lacia;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FakeDialog {
    private Context ctx;
    private String title = null;
    private CharSequence msg = null;
    private View view = null;
    private boolean useBack = true;

    private int color = Color.WHITE;
    private WindowManager mManager;
    private LinearLayout back;
    private FrameLayout dialog;

    private String nStr = null;
    private View.OnClickListener nLis;
    private String pStr = null;
    private View.OnClickListener pLis;

    private boolean inputEnabled = false;

    public FakeDialog(Context ctx) {
        this.ctx = ctx;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(CharSequence msg) {
        this.msg = msg;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void backgroundShadowEnable(boolean use) {
        this.useBack = use;
    }

    public void show() {
        int x = getWidth();
        final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                x, -1,
                getType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        if(inputEnabled) mParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        back = new LinearLayout(ctx);
        back.setOrientation(1);

        final LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(1);
        int pad = dip2px(10);

        if (this.title != null) {
            TextView title = new TextView(ctx);
            title.setText(this.title);
            title.setTextColor(Color.BLACK);
            title.setTextSize(21);
            title.setPadding(pad, pad, pad, pad);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
                        mManager.removeView(back);
                    } catch (Exception e) {
                        toast(e.toString());
                    }
                }
            });
            layout.addView(title);
        }
        if (msg != null) {
            ScrollView scroll = new ScrollView(ctx);
            LinearLayout layout2 = new LinearLayout(ctx);
            layout2.setOrientation(1);
            TextView txt = new TextView(ctx);
            txt.setText(this.msg);
            txt.setTextColor(Color.BLACK);
            txt.setTextSize(16);
            scroll.addView(txt);
            layout2.setPadding(pad, pad, pad, pad);
            layout2.addView(scroll);
            layout.addView(layout2);
        }

        if (this.view != null) {
            layout.addView(this.view);
        }

        layout.setLayoutParams(new LinearLayout.LayoutParams(x - dip2px(50), -2));
        back.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));

        FrameLayout layout0 = new FrameLayout(ctx);
        layout0.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
        layout0.addView(layout);

        if (nStr != null || pStr != null) {
            layout.setPadding(pad, pad, pad, dip2px(60));

            LinearLayout bLay = new LinearLayout(ctx);
            bLay.setPadding(pad, pad, pad, pad);
            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(-1, -2);
            param.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            bLay.setLayoutParams(param);

            if (nStr != null) addButton(bLay, nStr, nLis);
            if (pStr != null) addButton(bLay, pStr, pLis);

            layout0.addView(bLay);
        } else {
            layout.setPadding(pad, pad, pad, pad);
        }

        pad = dip2px(20);
        back.setPadding(pad, pad, pad, pad);
        back.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        if (useBack) back.setBackgroundColor(Color.argb(90, 0, 0, 0));
        back.addView(layout0);

        layout0.setBackgroundColor(this.color);
        ViewCompat.setElevation(layout0, dip2px(5));
        AlphaAnimation ani = new AlphaAnimation(0, 1);
        ani.setDuration(200);
        layout0.setAnimation(ani);
        back.startAnimation(ani);
        this.dialog = layout0;

        mManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        mManager.addView(back, mParams);

    }

    public void dismiss() {
        AlphaAnimation ani = new AlphaAnimation(1, 0);
        ani.setDuration(150);
        this.dialog.startAnimation(ani);
        this.back.startAnimation(ani);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
                mManager.removeView(back);
            }
        }, 140);
    }

    public void setNegativeButton(String str, View.OnClickListener lis) {
        this.nStr = str;
        this.nLis = lis;
    }

    public void setPositiveButton(String str, View.OnClickListener lis) {
        this.pStr = str;
        this.pLis = lis;
    }

    private void addButton(LinearLayout layout, String str, View.OnClickListener lis) {
        TextView btn = new TextView(ctx);
        btn.setText(str);
        btn.setTextSize(16);
        btn.setTextColor(Color.parseColor("#64B5F6"));
        int pad = dip2px(15);
        btn.setPadding(pad, pad, pad, pad);
        btn.setGravity(Gravity.RIGHT);
        if (lis != null) btn.setOnClickListener(lis);
        else btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        layout.addView(btn);
    }

    public void setBackgroundColor(int color) {
        this.color = color;
    }

    private int getType() {
        if (Build.VERSION.SDK_INT >= 26) return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else return WindowManager.LayoutParams.TYPE_PHONE;
    }

    private int getWidth() {
        int x = ctx.getResources().getDisplayMetrics().widthPixels;
        int y = ctx.getResources().getDisplayMetrics().heightPixels;
        if (x > y) return y;
        else return x;
    }

    public void setInputEnabled(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
    }


    private int dip2px(int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }

    private void toast(String msg) {
        Toast.makeText(ctx, msg, 1).show();
    }

}
