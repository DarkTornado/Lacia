package com.darktornado.lacia

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pos = intent.getStringExtra("pos")
        val data = intent.getStringExtra("data")
        val layout = LinearLayout(this);
        layout.orientation = 1

        val web = WebView(this)
        web.loadUrl("file:///android_asset/weather/index.html")
        web.settings.javaScriptEnabled = true
        web.layoutParams = LinearLayout.LayoutParams(-1, -1)
        web.setBackgroundColor(Color.TRANSPARENT)
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                web.loadUrl("javascript:applyData(\"$data\")")
                toast(pos + "의 날씨 정보를 불러왔습니다.")
                super.onPageFinished(view, url)
            }
        }
        layout.addView(web)
        layout.background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.parseColor("#4FC3F7"), Color.parseColor("#81D4FA"), Color.parseColor("#D1C4E9")))
        setContentView(layout)
    }

    fun toast(msg: String) {
        val toast = Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG)
        toast.view.setBackgroundColor(Lacia.getColor(190))
        val pad = dip2px(5)
        toast.view.setPadding(pad, pad, pad, pad)
        toast.show()
    }

    fun dip2px(dips: Int): Int {
        return Math.ceil((dips * this.resources.displayMetrics.density).toDouble()).toInt()
    }

}
