package com.darktornado.lacia

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.LinearLayout

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pos = intent.getStringExtra("pos")
        val data = intent.getStringExtra("data")
        val layout = LinearLayout(this);
        layout.orientation = 1
        val toolbar = Lacia.creatTitle(this, pos + " 날씨")
        setSupportActionBar(toolbar)
        layout.addView(toolbar)

        val web = WebView(this)
        web.loadData(data, "text/html; charset=UTF-8", null)
        web.setBackgroundColor(Color.WHITE)
        val mar = dip2px(20)
        val margin = LinearLayout.LayoutParams(-1, -1)
        margin.setMargins(mar, mar, mar, mar)
        web.layoutParams = margin
        layout.addView(web)

        layout.setBackgroundColor(Color.WHITE)

        setContentView(layout)
    }

    fun dip2px(dips: Int): Int {
        return Math.ceil((dips * this.resources.displayMetrics.density).toDouble()).toInt()
    }

}
