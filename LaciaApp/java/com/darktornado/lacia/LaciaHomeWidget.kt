package com.darktornado.lacia

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class LaciaHomeWidget : AppWidgetProvider() {

    override fun onUpdate(ctx: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(ctx, appWidgetManager, appWidgetIds)
        for (widgetId in appWidgetIds) {
            val remoteView = RemoteViews(ctx.packageName, R.layout.home_widget)
            val intent = Intent(ctx, MainActivity::class.java);
            intent.putExtra("input_start", true)
            remoteView.setOnClickPendingIntent(R.id.widget_button, PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
            appWidgetManager.updateAppWidget(widgetId, remoteView)
        }

    }

}
