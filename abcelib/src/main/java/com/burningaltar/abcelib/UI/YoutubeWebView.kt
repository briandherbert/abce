package com.burningaltar.abcelib.UI

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class YoutubeWebView (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : WebView(context, attrs, defStyleAttr) {
    val TAG = "YoutubeWebView"
    public fun setVideoUrl(url: String) {
        Log.v(TAG, "Setting url $url")

        var displayMetrics = Resources.getSystem().getDisplayMetrics()
        var width = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels)

        val frameVideo = "<html><body>Video From YouTube<br>" +
                "<iframe width=\"$width\" height=\"$width\" " +
                "src=\"$url\" frameborder=\"0\" allowfullscreen></iframe></body></html>"
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }
        val webSettings = settings
        webSettings.javaScriptEnabled = true
        loadData(frameVideo, "text/html", "utf-8")
    }
}
