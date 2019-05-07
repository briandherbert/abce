package com.burningaltar.abcelib

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class TextTagger {
    companion object {
        val TAG = "TextTagger"
        val HOST_BASE_URL = "http://c5398511.ngrok.io/analyze"

        interface TextTaggerListener {
            fun onGotTags(tags : ArrayList<GlobalTag>?)
        }

        fun getTags(str: String, context: Context, listener: TextTaggerListener) {

            val jsonStr = "{\"text\": \"$str\"}"

            Log.v(TAG, "Tagger request " + jsonStr)

            val jsonObj = JSONObject(jsonStr)

            val queue = Volley.newRequestQueue(context)
            val stringRequest = JsonObjectRequest(
                Request.Method.POST, HOST_BASE_URL, jsonObj,
                Response.Listener<JSONObject> { response ->
                    Log.v(ContentEngine.TAG, "success: " + response)

                    if (response != null) {
                        var result = Gson().fromJson(response.toString(), TextTaggerResponse::class.java)
                        Log.v(TAG, "response " + result.globalTags)
                        listener.onGotTags(result.globalTags)
                    }
                },
                Response.ErrorListener { error ->
                    Log.v(ContentEngine.TAG, "error:" + error.toString())
                })

            queue.add(stringRequest)

        }

    }

    data class GlobalTag(val text: String, val score: Double)
    data class TextTaggerResponse(val globalTags: ArrayList<GlobalTag>)
}