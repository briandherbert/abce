package com.example.brianherbert.trystuff.ce

import com.google.gson.JsonObject

data class Thought(
    var id: String,
    var thinkerId: Thinker,
    var intro: String,
    var tags: ArrayList<Tag>,
    var content: JsonObject
) {
    data class Tag(var type: String, var value: String, var relevance: Double, var displayText: String)
}