package com.burningaltar.abcelib

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.brianherbert.trystuff.ce.CEResponse
import com.example.brianherbert.trystuff.ce.Thinker
import com.google.gson.Gson
import org.json.JSONObject

class ContentEngine {
    companion object {
        val TAG = "CESearchActivity"
        val HOST_BASE_URL = "https://cool-bradley-EfW07T.bespoken.link"

        interface ContentEngineListener {
            fun onSearchResponse(response: CEResponse?)
        }

        fun search(keyword: String, context: Context, listener: ContentEngineListener) {
            search(arrayListOf(keyword), context, listener)
        }

        fun search(
            keywords: ArrayList<String>,
            context: Context,
            listener: ContentEngineListener,
            thinkers: ArrayList<Thinker> = ArrayList()
        ) {
            Log.v(TAG, "searching $keywords with thinkers $thinkers")

            var queryStr = ""
            var isFirst = true
            var usedWords = ArrayList<String>()
            for (keyword in keywords) {
                if (usedWords.contains(keyword)) continue
                usedWords.add(keyword)
                if (!isFirst) queryStr += ","
                isFirst = false
                queryStr += "{\"type\": \"KEYWORD_TAG\",\"value\": \"${keyword.toLowerCase()}\",\"relevance\": 1}"
            }

            var thinkersStr = ""

            if (!thinkers.isEmpty()) {
                thinkersStr = "\"cacheIds\": ["
                isFirst = true
                for (thinker in thinkers) {
                    if (!isFirst) thinkersStr += ","
                    thinkersStr += "\"${thinker.name}\""
                    isFirst = false
                }
                thinkersStr += "], "
            }

            val jsonStr =
                "{\"context\": {\"id\":\"xyz\",$thinkersStr\"tags\": [$queryStr]}, \"maxThoughts\":1}"

            Log.v(TAG, "full query $jsonStr")

            val jsonObj = JSONObject(jsonStr)

            val queue = Volley.newRequestQueue(context)
            val stringRequest = JsonObjectRequest(Request.Method.POST, HOST_BASE_URL, jsonObj,
                Response.Listener<JSONObject> { response ->
                    Log.v(TAG, "success: " + response)

                    if (response != null) {
                        var result = Gson().fromJson(response.toString(), CEResponse::class.java)
                        if (result.items != null) {
                            Log.v(TAG, "Num results " + result.items.size)
                            listener.onSearchResponse(result)
                        } else {
                            listener.onSearchResponse(null)
                        }
                    } else {
                        listener.onSearchResponse(null)
                    }
                },
                Response.ErrorListener { error ->
                    Log.v(TAG, "error:" + error.toString())
                    listener.onSearchResponse(null)
                })

            queue.add(stringRequest)
        }
    }

}