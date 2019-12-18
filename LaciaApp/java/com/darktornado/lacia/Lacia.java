 package com.darktornado.lacia;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Lacia {

    public static final String VERSION = "3.0";
    private static final String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static int getIcon(){
        return R.mipmap.drawer_open;
    }

    public static int getColor(){
        return Color.argb(255, 100, 181, 246);
    }

    public static int getColor(int alpha){
        return Color.argb(alpha, 100, 181, 246);
    }

    public static Toolbar creatTitle(Context ctx, String title){
        Toolbar toolbar = new Toolbar(ctx);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Lacia.getColor());
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -2);
        //margin.setMargins(0, 0, 0, dip2px(8));
        toolbar.setLayoutParams(margin);
        ViewCompat.setElevation(toolbar, dip2px(ctx,5));
        return toolbar;
    }

    public static String[] getAllAudio(Context ctx) {
        String[] result = null;
        try {
            Cursor cursor = ctx.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DATA}, null, null, null);
            if (cursor.getCount() == 0) return null;
            result = new String[cursor.getCount()];
            cursor.moveToFirst();
            result[cursor.getPosition()] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            while (cursor.moveToNext()) {
                result[cursor.getPosition()] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        } catch (Exception e) {
            //toast(e.toString());
        }
        return result;
    }


    public static String readAsset(Context ctx, String name) {
        try {
            AssetManager am = ctx.getAssets();
            InputStream stream = am.open(name);
            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(isr);
            String str = br.readLine();
            String line = "";
            while ((line = br.readLine()) != null) {
                str += "\n" + line;
            }
            isr.close();
            br.close();
            return str;
        } catch (Exception e) {
            //toast(e.toString());
        }
        return "";
    }


    public static String getReply(String[] data, String input) {
        ChatModule cm = new ChatModule();
        cm.setData(data);
        cm.inputChat(input);
        String[] result = cm.getResult();
        if (result == null) return null;
        int r = (int) Math.floor(Math.random() * result.length);
        return result[r];
    }

    public static String getDataFromServer(String link){
        try{
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            if(con!=null) {
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String str = br.readLine();
                String line = "";
                while((line = br.readLine()) != null){
                    str += "\n" + line;
                }
                br.close();
                isr.close();
                return str;
            }
        }
        catch(Exception e) {
            //toast(e.toString());
        }
        return null;
    }

    public static String[] getWeather(String pos){
        try{
            String data = getDataFromServer("https://m.search.naver.com/search.naver?query="+pos.replace(" ", "%20")+"%20날씨").replaceAll("(<([^>]+)>)", "");
            String[] result = new String[2];
            data = data.split("월간")[1].split("현재날씨")[0];

            String[] data2 = data.split("주간날씨")[1].split("날씨 및 강수확률")[1].split("   ");
            result[1] = data2[1].replaceFirst(" ", " 강수확률 : ").replace("퍼센트 ", "% (")+")\n";
            result[1] += data2[2].replaceFirst(" ", " 강수확률 : ").replace("퍼센트 ", "% (")+")\n";

            data = data.split("시간별 예보")[0];
            result[0] = data.split("현재온도")[0].trim();
            String[] data1 = data.split("현재온도")[1].trim().split(" ");
            result[1] += "현재 온도 : "+data1[0]+"℃\n"+data1[1].replace("온도", " 온도 : ")+"℃\n"+data1[10].replace("먼지", "먼지 : ").replace("㎍/㎥", "μg/m³");

            return result;
        }
        catch(Exception e){
            //toast(e.toString());
        }
        return null;
    }

    private static int dip2px(Context ctx, int dips) {
        return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
    }

    public static void initSettings(){
        new File(sdcard+"/Lacia/").mkdirs();
        if(readData("useJs")==null) saveSettings("useJs", true);
        if(readData("useZoom")==null) saveSettings("useZoom", true);
    }

    public static String readData(String name){
        return readFile(sdcard+"/Lacia/"+name+".txt");
    }

    public static void saveData(String name, String value){
        saveFile(sdcard+"/Lacia/"+name+".txt", value);
    }

    public static boolean loadSettings(String name) {
        String cache = readData(name);
        if (cache == null) return false;
        return cache.equals("true");
    }

    public static void saveSettings(String name, boolean onoff) {
        saveData(name, String.valueOf(onoff));
    }


    public static String readFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String str = br.readLine();
            String line = "";
            while ((line = br.readLine()) != null) {
                str += "\n" + line;
            }
            fis.close();
            isr.close();
            br.close();
            return str;
        } catch (Exception e) {
            //toast(e.toString());
        }
        return "";
    }

    public static void saveFile(String path, String value) {
        try {
            File file = new File(path);
            FileOutputStream fos = new java.io.FileOutputStream(file);
            fos.write(value.getBytes());
            fos.close();
        } catch (Exception e) {
            //toast(e.toString());
        }
    }

}
