package com.darktornado.utils

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.Toast
import com.darktornado.lacia.Lacia
import com.darktornado.lacia.R
import org.jsoup.Jsoup
import org.json.JSONObject
import org.jsoup.select.Elements

class BusActivity : AppCompatActivity() {

    var web: WebView? = null
    var busId: String? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 0, 0, "새로 고침").setIcon(R.drawable.web_reload).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            when (item.itemId) {
                0 ->
                    Thread {
                        val result = getBusInfo();
                        if (getBusInfo() != "") runOnUiThread({
                            web?.loadData(result, "text/html; charset=UTF-8", null);
                        });
                    }.start()
            }
        } catch (e: Exception) {
            toast(e.toString())
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this)
        layout.orientation = 1
        val toolbar = Lacia.creatTitle(this, "실시간 버스 운행 정보")
        setSupportActionBar(toolbar)
        layout.addView(toolbar)

        val web = WebView(this)
        val mar = dip2px(20)
        val margin = LinearLayout.LayoutParams(-1, -1)
        margin.setMargins(mar, mar, mar, mar)
        web.layoutParams = margin
        layout.addView(web)
        val input = intent.getStringExtra("input")
        if (input == null) {
            toast("비정상적인 액티비티 실행이 감지되었습니다.\n잡았다, 요놈!")
            finish()
        }
        Thread {
            val result = initBusInfo(input)
            if (result != "") runOnUiThread({
                web.loadData(result, "text/html; charset=UTF-8", null);
            });
        }.start()
        this.web = web
        layout.setBackgroundColor(Color.WHITE)
        setContentView(layout)
    }

    fun initBusInfo(input: String?): String {
        try {
            val url = "https://m.map.kakao.com/actions/searchView?q=" + input?.replace(" ", "%20")
                    ?.replaceFirst("직행", "직행%20")?.replaceFirst("광역", "광역%20") + "%20버스";
            val data = Jsoup.connect(url).ignoreContentType(true).get()
            busId = data.select("div.search_result_wrap").select("li").get(0).attr("data-id");
            if (busId.equals("")) {
                toast("해당 버스를 찾을 수 없습니다")
                finish()
                return ""
            }
            return getBusInfo();
        } catch (e: Exception) {
//            return e.toString()
            toast("버스 정보를 뜯어오는 과정에서 오류가 발생한거에요.")
            finish()
            return "";
        }
    }

    fun getBusInfo(): String {
        try {
            val data1 = Jsoup.connect("https://m.map.kakao.com/actions/busDetailInfoJson?busId=$busId").ignoreContentType(true).get().wholeText()
            val data2 = JSONObject(data1).getJSONArray("busLocationList")
            val count = data2.length()
            val busInfo = arrayOfNulls<BusInfo>(count)
            for (n in 0 until count) {
                val data3 = data2.getJSONObject(n)
                val pos = (data3["sectionOrder"] as String).toInt() - 2
                val seat = (data3["remainSeat"] as String).toInt()
                busInfo[n] = BusInfo(pos, seat)
            }
            val data = Jsoup.connect("https://m.map.kakao.com/actions/busDetailInfo?busId=$busId").ignoreContentType(true).get()
            val list = data.select("ul.list_route").select("li")
            val result = getBusInfoTable(list, busInfo)
            toast(count.toString() + "대 운행 중")
            return "<meta name=\"viewport\" content=\"user-scalable=no width=device-width\" />" +
                    "<style>table{border-top: 1px solid #000000; color: #000000; border-collapse: collapse;}td{border-bottom: 1px solid #000000; padding : 7px;}</style>" +
                    "<table border=1 width=100%>$result</table>"
        } catch (e: Exception) {
//            toast(e.toString())
            toast("버스 정보를 뜯어오는 과정에서 오류가 발생한거에요.")
            finish()
            return "";
        }
    }


    private fun getBusInfoTable(list: Elements, busInfo: Array<BusInfo?>): String {
        val result = StringBuilder()
        val icons = "↓◆".toCharArray()
        for (n in 0 until list.size) {
            var name = list.get(n).select("strong.tit_route").text()
            val stopId = list.get(n).select("span.txt_route").text().trim()
            if (stopId == "미정차") name = "<font color=#9E9E9E>$name (미정차)</font>"
            var index = 0
            var seat = -1
            for (bi in busInfo) {
                if (bi!!.pos == n) {
                    seat = bi.seat
                    index = 1
                    break
                }
            }
            result.append("<tr><td align=center>" + icons[index] + "</td>")
            if (seat == -1) result.append("<td colspan=2>$name</td></tr>")
            else result.append("<td>$name</td><td style=\"padding:5px 1px 5px 1px\" align=center>${seat}석</td></tr>")
        }
        return result.toString()
    }

    fun toast(msg: String) {
        runOnUiThread {
            val toast = Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG)
            toast.view.setBackgroundColor(Lacia.getColor(190))
            val pad = dip2px(5)
            toast.view.setPadding(pad, pad, pad, pad)
            toast.show()
        }
    }

    fun dip2px(dips: Int): Int {
        return Math.ceil((dips * this.resources.displayMetrics.density).toDouble()).toInt()
    }

}
