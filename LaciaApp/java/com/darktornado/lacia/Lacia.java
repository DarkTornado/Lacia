package com.darktornado.lacia;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

 public class Lacia {

     public static final String VERSION = "3.2";
     public static final String COPYRIGHT_YEAR = "2019-2021";
     public static final String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
     public static final String NOTI_CHANNEL_MAIN = "lacia_main_channel";
     public static final int NOTI_ID_MAIN_SERVICE = 1;
     public static final int NOTI_ID_MUSIC_SERVICE = 2;

     public static int getIcon() {
         return R.mipmap.drawer_open;
     }

     public static int getColor() {
         return Color.argb(255, 100, 181, 246);
     }

     public static int getColor(int alpha) {
         return Color.argb(alpha, 100, 181, 246);
     }

     public static Toolbar creatTitle(Context ctx, String title) {
         Toolbar toolbar = new Toolbar(ctx);
         toolbar.setTitle(title);
         toolbar.setTitleTextColor(Color.WHITE);
         toolbar.setBackgroundColor(Lacia.getColor());
         LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -2);
         //margin.setMargins(0, 0, 0, dip2px(8));
         toolbar.setLayoutParams(margin);
         ViewCompat.setElevation(toolbar, dip2px(ctx, 5));
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

     public static String[][] getAllApps(Context ctx) {
         try {
             Intent intent = new Intent(Intent.ACTION_MAIN, null);
             intent.addCategory(Intent.CATEGORY_LAUNCHER);
             PackageManager pm = ctx.getPackageManager();
             List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
             String[][] appList = new String[apps.size()][];
             for (int n = 0; n < apps.size(); n++) {
                 ResolveInfo pack = apps.get(n);
                 appList[n] = new String[2];
                 appList[n][0] = pack.loadLabel(pm).toString();
                 appList[n][1] = pack.activityInfo.applicationInfo.packageName;
             }
             return appList;
         } catch (Exception e) {
             //toast(e.toString());
         }
         return null;
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

     public static String getDataFromServer(String link) {
         try {
             URL url = new URL(link);
             URLConnection con = url.openConnection();
             if (con != null) {
                 con.setConnectTimeout(5000);
                 con.setUseCaches(false);
                 InputStreamReader isr = new InputStreamReader(con.getInputStream());
                 BufferedReader br = new BufferedReader(isr);
                 String str = br.readLine();
                 String line = "";
                 while ((line = br.readLine()) != null) {
                     str += "\n" + line;
                 }
                 br.close();
                 isr.close();
                 return str;
             }
         } catch (Exception e) {
             //toast(e.toString());
         }
         return null;
     }

     public static String[] getWeather(String pos) {
         try {
             Elements data = Jsoup.connect("https://m.search.naver.com/search.naver?query=" + pos.replace(" ", "+") + "+날씨").get().select("div.weather_info");
             String status = data.select("div.weather_main").get(0).text();
             Elements temp = data.select("strong");
             String tempCurrent = temp.get(0).text();
             String tempMax = temp.get(1).text();
             String tempMin = temp.get(2).text();
             String tempWind = temp.get(3).text();
             Elements table = data.select("span.figure_result");
             String dust, hum, wind;
             if (table.size() == 6) {
                 dust = table.get(1).text();
                 hum = table.get(4).text();
                 wind = data.select("span.figure_text").get(5).text() + ", " + table.get(5).text();
             } else {
                 dust = table.get(0).text();
                 hum = table.get(3).text();
                 wind = data.select("span.figure_text").get(4).text() + ", " + table.get(4).text();
             }
             String result = tempCurrent.replace("온도", "온도 : ") + "\n";
             result += "체감 온도 : " + tempWind + "\n";
             result += "최고 기온 : " + tempMax + "\n";
             result += "최저 기온 : " + tempMin + "\n";
             result += "습도 : " + hum + "%\n";
             result += "바람 : " + wind + "m/s\n";
             result += "미세먼지 : " + dust + "μg/m³";
             return new String[]{status, result.replace("°", "℃")};
         } catch (Exception e) {
             //toast(e.toString());
         }
         return null;
     }

     private static int dip2px(Context ctx, int dips) {
         return (int) Math.ceil(dips * ctx.getResources().getDisplayMetrics().density);
     }

     public static void initSettings() {
         new File(sdcard + "/Lacia/").mkdirs();
         if (readData("useJs") == null) saveSettings("useJs", true);
         if (readData("useZoom") == null) saveSettings("useZoom", true);
     }

     public static String readData(String name) {
         return readFile(sdcard + "/Lacia/" + name + ".txt");
     }

     public static void saveData(String name, String value) {
         saveFile(sdcard + "/Lacia/" + name + ".txt", value);
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

     public static Notification.Builder createNotifation(Context ctx, String channel, String name) {
         if (Build.VERSION.SDK_INT < 26) return new Notification.Builder(ctx);
         NotificationChannel nc = new NotificationChannel(channel, name, NotificationManager.IMPORTANCE_LOW);
         NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
         if (nm != null) nm.createNotificationChannel(nc);
         return new Notification.Builder(ctx, channel);
     }

 }
