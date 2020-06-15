package com.darktornado.lacia;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class MusicService extends Service {

    public MediaPlayer media = new MediaPlayer();

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Notification.Builder noti = Lacia.createNotifation(this, Lacia.NOTI_CHANNEL_MAIN, "Lacia Music Service");
            noti.setSmallIcon(R.mipmap.icon);
            noti.setContentTitle("Lacia Music Player");
            noti.setContentText("Music is Playing...");
            noti.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MusicActivity.class), 0));
            startForeground(Lacia.NOTI_ID_MUSIC_SERVICE, noti.build());
            randomMusic(intent.getStringExtra("music"));
        } catch (Exception e) {
            toast(e.toString());
        }
        return START_NOT_STICKY;
    }

    private void randomMusic(String music) {
        try {
            String[] musics = Lacia.getAllAudio(this);
            if (musics == null) {
                toast("기기에서 음악 파일을 찾을 수 없습니다.");
            } else {
                if (music == null) music = musics[(int) Math.floor(Math.random() * musics.length)];
                media.reset();
                media.setDataSource(music);
                media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        randomMusic(null);
                    }
                });
                media.prepare();
                media.start();
            }
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        media.reset();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int dip2px(int dips) {
        return (int) Math.ceil((double)(((float) dips) * getResources().getDisplayMetrics().density));
    }

    public void toast(final String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.getView().setBackgroundColor(Lacia.getColor(190));
        int pad = dip2px(5);
        toast.getView().setPadding(pad, pad, pad, pad);
        toast.show();
    }
}
