package com.burningaltar.abcelib

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import com.example.brianherbert.trystuff.ce.Thought
import com.example.brianherbert.trystuff.ce.ThoughtContentManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_thought.*

class ThoughtActivity : Activity() {
    companion object {
        val TAG = "ThoughtActivity"

        var mMediaPlayer = MediaPlayer()


        val EXTRA_THOUGHT = "thought"
        fun createIntent(activity: Activity, thought: Thought): Intent {
            val intent = Intent(activity, ThoughtActivity::class.java)
            intent.putExtra(EXTRA_THOUGHT, Gson().toJson(thought))
            return intent
        }

        fun launch(activity: Activity, thought: Thought) {
            Log.v(TAG, "Launch thought " + thought.id + " from " + activity)
            activity.startActivity(createIntent(activity, thought))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thought)

        var thought: Thought? = null
        if (intent != null && intent.hasExtra(EXTRA_THOUGHT)) {
            thought = Gson().fromJson<Thought>(intent.extras.getString(EXTRA_THOUGHT), Thought::class.java)
        }

        if (thought == null) {
            throw IllegalArgumentException("No thought found!")
        }

        container_thought_background.setOnClickListener { finish() }

        renderThought(thought)
    }

    fun renderThought(thought: Thought) {
        Log.v(TAG, "render thought " + thought.cacheId)

        var content = ThoughtContentManager.getThoughtContent(thought)
        if (content == null) throw IllegalArgumentException("Unable to parse thought!")

        lbl_thought_title.text = content.getTitleText()

        if (!TextUtils.isEmpty(content.getContentText())) {
            lbl_thought_content.visibility = View.VISIBLE
            lbl_thought_content.text = content.getContentText()
            lbl_thought_content.movementMethod = ScrollingMovementMethod()
        }

        var youtubeId = content.getYoutubeId()
        var audioUrl = content.getAudioStreamUrl()
        var seekTo = content.getSeekToTime()

        if (!TextUtils.isEmpty(youtubeId)) {
            webview_youtube.visibility = View.VISIBLE
            webview_youtube.setVideoUrl(youtubeId!!, (seekTo / DateUtils.SECOND_IN_MILLIS).toInt())
        } else if (!TextUtils.isEmpty(audioUrl)) {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer.setDataSource(audioUrl)
            mMediaPlayer.prepare()
            mMediaPlayer.setOnPreparedListener {
                mMediaPlayer.seekTo(seekTo)
                mMediaPlayer.setOnSeekCompleteListener {
                    btn_thought_play_pause.visibility = View.VISIBLE
                    btn_thought_play_pause.setOnClickListener { playPause() }
                    playPause()
                }
            }
        }
    }

    fun playPause() {
        if (mMediaPlayer.isPlaying) {
            btn_thought_play_pause.setImageResource(R.drawable.ic_play)
            mMediaPlayer.pause()
        } else {
            btn_thought_play_pause.setImageResource(R.drawable.ic_pause)
            mMediaPlayer.start()
        }
    }

    override fun onDestroy() {
        mMediaPlayer.release()
        super.onDestroy()
    }
}