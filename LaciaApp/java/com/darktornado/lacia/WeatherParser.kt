package com.darktornado.lacia

import android.content.Context
import android.util.Log
import org.jsoup.Jsoup
import java.lang.Exception
import java.lang.StringBuilder

class WeatherParser(val ctx: Context, val pos: String) {

    fun parse(): Pair<String, String>? {
        val zoneId = getZoneIdByName(pos) ?: return null
        try {
            val data0 = Jsoup.connect("https://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=$zoneId")
                    .get()
            val location = data0.select("category").text()
            val result = StringBuilder()
            val data = data0.select("body").select("data")
            val days = arrayOf("오늘", "내일", "모래")
            var n = 0;
            while (n < data.size) {
                val datum = data.get(n);
                result.append("<tr align=center><td class=title colspan=4>")
                        .append(days[datum.select("day").text().toInt()] + " ")
                        .append(datum.select("hour").text() + "시 : ")
                        .append(datum.select("wfKor").text())
                        .append("</td></tr>")
                val wind = (Math.round(datum.select("ws").text().toDouble() * 10) / 10).toString() + "m/s, " + datum.select("wdEn").text()
                result.append("<tr align=center>")
                        .append("<td width=25%><img src='./images/temp.png' width=60%><br>" + datum.select("temp").text() + "°C</td>")
                        .append("<td width=25%><img src='./images/hum.png' width=60%><br>" + datum.select("reh").text() + "%</td>")
                        .append("<td width=25%><img src='./images/rain.gif' width=60%><br>" + datum.select("pop").text() + "%</td>")
                        .append("<td width=25%><img src='./images/wind.gif' width=60%><br>$wind</td>")
                result.append("</tr>")
                n++
            }
            return Pair(location, result.toString())
        } catch (e: Exception) {
            Log.i("_lacia", e.toString());
            return null
        }
    }

    fun getZoneIdByName(name: String): String? {
        val data: List<String> = Lacia.readAsset(ctx, "weather/zone_id.csv").split("\n");
        for (_data in data) {
            val datum = _data.split(",".toRegex()).toTypedArray()
            if (datum[0] == name) return datum[1]
        }
        for (_data in data) {
            val datum = _data.split(",".toRegex()).toTypedArray()
            if (datum[0].contains(name)) return datum[1]
        }
        return null
    }

}