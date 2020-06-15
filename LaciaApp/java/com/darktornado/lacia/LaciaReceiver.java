package com.darktornado.lacia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.widget.Toast;

public class LaciaReceiver extends BroadcastReceiver {

    private Context ctx;
    private Handler handler = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            ctx = context;
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                if (!Lacia.loadSettings("useWindow")) return;
                try {
                    toast("[Lacia] 데이터베이스 불러오는 중...");
                    String[] chatData = Lacia.readAsset(ctx, "chatData.txt").split("\n");
                    startService(context, chatData);
                } catch (Exception e) {
                    toast("[Lacia] 데이터베이스 로드 실패\n오류 : " + e.toString());
                }
            }
        }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startService(Context ctx, String[] chatData){
        Intent intent = new Intent(ctx, MainService.class);
        intent.putExtra("chat_data", chatData);
        ctx.startService(intent);
        toast("[Lacia] 상시 대기가 실행되었습니다.");
    }

    public void toast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
                toast.getView().setBackgroundColor(Lacia.getColor(190));
                int pad = dip2px(5);
                toast.getView().setPadding(pad, pad, pad, pad);
                toast.show();
            }
        });
    }

    private int dip2px(int dips){
        return (int)Math.ceil(dips*ctx.getResources().getDisplayMetrics().density);
    }

    public void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }
}
