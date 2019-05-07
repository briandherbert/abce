package com.burningaltar.abce

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.burningaltar.abcelib.ContentEngine
import com.burningaltar.abcelib.ThoughtActivity
import com.burningaltar.abcelib.UI.TagsTextView
import com.example.brianherbert.trystuff.ce.CEResponse
import com.example.brianherbert.trystuff.ce.Thought
import com.example.brianherbert.trystuff.ce.ThoughtContentManager
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_ce_search.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.ContextThemeWrapper
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.brianherbert.trystuff.ce.Thinker
import java.lang.IllegalArgumentException


class ABCEDemoActivity : Activity(), ContentEngine.Companion.ContentEngineListener, TagsTextView.TagClickedListener,
    ThoughtsRVAdapter.ThoughtClickedListener {

    val TAG = "ABCEDemoActivity"

    override fun onTagClicked(tag: String) {
        txt_search.setText(tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ce_search)

        btn_search.setOnClickListener { search() }

        btn_add.setOnClickListener {
            if (!txt_search.text.isEmpty()) {
                var chip = Chip(this)
                chip.text = txt_search.text.toString().trim()
                chipgroup_search.addView(chip)
                txt_search.text.clear()
            }
        }

        rv_thoughts.layoutManager = LinearLayoutManager(this)

        txt_search.requestFocus()

        for (thinker in Thinker.values()) {
            var chip = Chip(this)
            chip.text = thinker.name
            chip.chipBackgroundColor = resources.getColorStateList(R.color.chip_bg, theme)
            chipgroup_thinkers.addView(chip)
            chip.tag = thinker
            chip.isSelected = true

            chip.setOnClickListener { chip.isSelected = !chip.isSelected }
        }
    }

    fun search() {
        var keywords = ArrayList<String>()
        var curSearch = txt_search.text.toString().trim()
        if (!TextUtils.isEmpty(curSearch)) keywords.add(curSearch)
        for (v in chipgroup_search.children) {
            keywords.add((v as Chip).text.toString())
        }

        var thinkers = ArrayList<Thinker>()
        for (v in chipgroup_thinkers.children) {
            if (v.isSelected) thinkers.add(v.tag as Thinker)
        }

        if (!keywords.isEmpty()) {
            ContentEngine.search(keywords, this, this, thinkers)

            btn_search.isEnabled = false
            btn_add.isEnabled = false

            lbl_last_query.text = "Query: " + keywords.toString()
        }

        // Dismiss keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(txt_search.getWindowToken(), 0)
    }

    override fun onSearchResponse(response: CEResponse?) {
        btn_search.isEnabled = true
        btn_add.isEnabled = true

        chipgroup_search.removeAllViews()
        txt_search.text.clear()

        if (response == null) {
            Log.v(TAG, "Null response")
            return
        }

        rv_thoughts.adapter = ThoughtsRVAdapter(response.thoughts, this, this)
        Log.v(TAG, response!!.toString())
    }

    override fun onThoughtClicked(thought: Thought) {
        ThoughtActivity.launch(this, thought)
    }
}