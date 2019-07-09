package com.example.brianherbert.trystuff.ce

import android.text.format.DateUtils
import android.util.Log
import com.burningaltar.abcelib.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.lang.Exception

class ThoughtContentManager {
    companion object {
        val TAG = "ThoughtContent"
        val gson = Gson()

        fun getDescription(thought: Thought) : String? {
            return getThoughtContent(thought.cacheId, thought.content)?.getTitleText()
        }

        fun getThoughtContent(thought: Thought) : ThoughtContent? {
            return Companion.getThoughtContent(thought.cacheId, thought.content)
        }

        fun getThoughtContent(thinker: Thinker, json: JsonObject) : ThoughtContent? {
            var klass : Class<out ThoughtContent>? = null

            when (thinker) {
                Thinker.sermon_snippets -> klass = SermonSnippetContent::class.java
                Thinker.reading_plans -> klass = DevotionContent::class.java
                Thinker.prayers -> klass = PrayerContent::class.java
                Thinker.reading_plan_days -> klass = ReadingPlanDayContent::class.java
                Thinker.songs -> klass = SongContent::class.java
                Thinker.verses -> klass = VersesContent::class.java
            }

            if (klass != null) {
                var content = gson.fromJson(json, klass)

                if (content.type != null) {
                    try {
                        content.type = ContentType.valueOf(json.get("contentType").asString)
                    } catch (e: Exception) {
                        Log.v(TAG, "error parsing content type", e)
                        Log.v(TAG, "json is " + json)
                    }
                }

                return content
            }

            return null
        }
    }

    enum class ContentType(val defaultIcon : Int) {
        audio(R.drawable.ic_audio),
        video(R.drawable.ic_video)
    }

    abstract class ThoughtContent() {
        var type : ContentType = ContentType.video

        abstract fun getTitleText(): String

        abstract fun getContentText() : String

        open fun getAudioStreamUrl() : String? {
            return null
        }

        open fun getSeekToTime() : Int {
            return 0
        }

        open fun getYoutubeId() : String? {
            return null
        }
    }

    class SermonSnippetContent(
                               val sermonInfo: SermonInfo,
                               val contentType: String,
                               val url: String,
                               val startTimeSeconds: Double,
                               val endTimeSeconds: Double)
        : ThoughtContent() {

        data class SermonInfo(var id: String, var pastorName: String, var sermonName: String, var title: String, var youtubeVideoId:String)

        override fun getTitleText(): String {
            return "\"${sermonInfo.title}\" - ${sermonInfo.pastorName}"
        }

        override fun getContentText(): String {
            return "Listen"
        }

        override fun getAudioStreamUrl() : String {
            return "https://s3.amazonaws.com/labs.content-engine-media/sermons/${sermonInfo.id}.mp3"
        }

        override fun getSeekToTime(): Int {
            return (startTimeSeconds * DateUtils.SECOND_IN_MILLIS).toInt()
        }

        override fun getYoutubeId(): String? {
            return sermonInfo.youtubeVideoId
        }
    }

    class DevotionContent(
            val passage: String,
            val followup: String)
        : ThoughtContent() {

        override fun getTitleText(): String {
            return "Devotion: $passage"
        }

        override fun getContentText(): String {
            return passage
        }
    }

    class PrayerContent(
            val who: String,
            val where: String,
            @SerializedName("when") val wen: String,
            val text: String)
        : ThoughtContent() {

        override fun getTitleText(): String {
            return "Prayer by $who from $where"
        }

        override fun getContentText(): String {
            return text
        }
    }

    class ReadingPlanDayContent(
            val date: String,
            val dayNum: Int,
            val references: ArrayList<String>,
            val text: String,
            val title: String)
        : ThoughtContent() {

        override fun getTitleText(): String {
            return title
        }

        override fun getContentText(): String {
            return text
        }
    }

    class SongContent(
            val id: String,
            val text: String,
            val songName: String,
            val lyrics: String,
            val artist: String)
        : ThoughtContent() {

        override fun getTitleText(): String {
            return "\"$songName\" - $artist"
        }

        override fun getContentText(): String {
            return lyrics
        }

        override fun getAudioStreamUrl() : String {
            return "https://s3.amazonaws.com/labs.content-engine-media/songs/$id.mp3"
        }
    }

    class VersesContent(
        val ref: String,
        val humanStr: String,
        val nlt_text: String)
        : ThoughtContent() {

        override fun getTitleText(): String {
            return ref
        }

        override fun getContentText(): String {
            return "$humanStr\n$nlt_text"
        }
    }
}