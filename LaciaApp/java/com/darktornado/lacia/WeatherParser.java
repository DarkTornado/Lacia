package com.darktornado.lacia;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WeatherParser {
    private String pos;

    public WeatherParser(String pos) {
        this.pos = pos;
    }

    public String getData() {
        try {
            Elements data = Jsoup.connect("https://m.search.daum.net/search?q=날씨%20" + pos.replace(" ", "%20")).get()
                    .select("div#weatherPanels").select("div.wrap_pannel");

            return "<meta name='viewport' content='user-scalable=no width=device-width' />" +
                    "<style>td{padding:5px;}table{border: 1px solid #000000;border-collapse: collapse;}</style>" +
                    "<table width=100% border=1>" +
                    parseCurrentWeather(data.get(0)) +
                    getWeeklyWeather() +
                    "</table>";
        } catch (Exception e) {
            return null;
        }
    }

    private String parseCurrentWeather(Element data) {
        String temp = data.select("em.txt_temp").first().ownText() + "℃";
        String status = data.select("p.desc_main").text().split(", 어제")[0];

        Elements data2 = data.select("ul.list_detail").select("li");
        String dust, dust2;
        try {
            dust = data2.get(1).select("span.txt_state").text() + " (" + data2.get(1).select("span.txt_num").first().ownText() + "μg/m³)";
        } catch (Exception e) {
            dust = "정보 없음";
        }
        try {
            dust2 = data2.get(0).select("span.txt_state").text() + " (" + data2.get(0).select("span.txt_num").first().ownText() + "μg/m³)";
        } catch (Exception e) {
            dust2 = "정보 없음";
        }

        String rain = "정보 없음";
        String windSpeed = "정보 없음";
        String windDir = "정보 없음";
        String hum = "정보 없음";
        Elements rainList = data.select("div.area_rain").select("li");
        Elements windList = data.select("div.area_wind").select("li");
        Elements humList = data.select("div.area_damp").select("li");

        for (int n = 0; n < rainList.size(); n++) {
            if (rainList.get(n).attr("class").contains(" on")) {
                rain = rainList.get(n).select("span.txt_emph").text();
                windDir = windList.get(n).select("span.ico_wind").text();
                windSpeed = windList.get(n).select("span.txt_num").first().ownText() + "m/s";
                hum = humList.get(n).select("span.txt_num").text();
                break;
            }
        }

        return "<tr align=center><td colspan=2><b><big>현재 날씨</big></b></td></tr>" +
                "<tr align=center><td width=40%><b>상태</b></td><td>" + status + "</td></tr>" +
                "<tr align=center><td><b>온도</b></td><td>" + temp + "</td></tr>" +
                "<tr align=center><td><b>습도</b></td><td>" + hum + "</td></tr>" +
                "<tr align=center><td><b>바람</b></td><td>" + windDir + ", " + windSpeed + "</td></tr>" +
                "<tr align=center><td><b>강수확률</b></td><td>" + rain + "</td></tr>" +
                "<tr align=center><td><b>미세먼지</b></td><td>" + dust + "</td></tr>" +
                "<tr align=center><td><b>초미세먼지</b></td><td>" + dust2 + "</td></tr>";
    }

    private String getWeeklyWeather(){
        try {
            Elements data = org.jsoup.Jsoup.connect("https://search.naver.com/search.naver?query=" + pos.replace(" ", "+") + "날씨")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36").get()
                    .select("div[class=table_info weekly _weeklyWeather]").select("ul").select("li");

            String[] days = {null, "내일", "모래", "글피"};
            StringBuilder result = new StringBuilder();
            for (int n = 1; n < days.length; n++) {
                Elements rain = data.get(n).select("span.num");
                String[] temp = data.get(n).select("dd").text().replace("°", "℃").split("/");
                result.append("<tr align=center><td colspan=2><b><big>").append(days[n]).append(" 날씨</big></b></td></tr>");
                result.append("<tr align=center><td><b>강수확률</b></td><td>").append(rain.get(0).text()).append("% → ").append(rain.get(1).text()).append("%</td></tr>");
                result.append("<tr align=center><td><b>최저기온</b></td><td>").append(temp[0]).append("</td></tr>");
                result.append("<tr align=center><td><b>최고기온</b></td><td>").append(temp[1]).append("</td></tr>");
            }
            return result.toString();
        } catch (Exception e) {
            return null;
        }
    }


}
