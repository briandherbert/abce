package com.burningaltar.abcelib.UI

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class YoutubeWebView : WebView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val TAG = "YoutubeWebView"

    //"https://www.youtube.com/embed/xyVL8lXMcJk/start=658&end=808"

    fun setVideoUrl(youtubeId: String, startTime : Int = 0) {
        Log.v(TAG, "Setting url $url")

        var displayMetrics = Resources.getSystem().getDisplayMetrics()
        var width = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels)

        width = 300

        Log.v("blarg", "width " + width + " start time " + startTime + " video id " + youtubeId)

        val frameVideo = "<html><body>Video From YouTube<br>" +
                "<iframe width=\"100%\" height=\"90%\" " +
                "src=\"https://www.youtube.com/embed/$youtubeId/?start=$startTime&autoplay=1\" frameborder=\"0\"></iframe></body></html>"
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
