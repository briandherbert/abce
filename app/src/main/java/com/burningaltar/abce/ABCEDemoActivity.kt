package com.burningaltar.abce

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.burningaltar.abcelib.ContentEngine
import com.burningaltar.abcelib.ThoughtActivity
import com.burningaltar.abcelib.UI.TagsTextView
import com.example.brianherbert.trystuff.ce.CEResponse
import com.example.brianherbert.trystuff.ce.Thought
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_ce_search.*
import android.view.inputmethod.InputMethodManager
import com.burningaltar.abcelib.TextTagger
import com.example.brianherbert.trystuff.ce.Thinker
import java.lang.Exception


class ABCEDemoActivity : Activity(), ContentEngine.Companion.ContentEngineListener, TagsTextView.TagClickedListener,
    ThoughtsRVAdapter.ThoughtClickedListener, TextTagger.Companion.TextTaggerListener {

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
                addTagChip(txt_search.text.toString())
            }
        }

        btn_tags.setOnClickListener { TextTagger.getTags(txt_search.text.toString(), this, this) }

        rv_thoughts.layoutManager = LinearLayoutManager(this)

        txt_search.requestFocus()

        // Thinker filter chips
        for (thinker in Thinker.values()) {
            var chip = Chip(this)
            chip.text = thinker.name
            chip.chipBackgroundColor = resources.getColorStateList(R.color.thinker_chip_bg, theme)
            chipgroup_thinkers.addView(chip)
            chip.tag = thinker

            chip.setOnClickListener { chip.isSelected = !chip.isSelected }
        }

        checkShareIntent()
    }

    fun search() {
        var keywords = ArrayList<String>()
        var curSearch = txt_search.text.toString().trim()

        if (!TextUtils.isEmpty(curSearch)) {
            if (curSearch.contains(" ")) {
                keywords.addAll(curSearch.split(" "))
            } else {
                keywords.add(curSearch)
            }
        }
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

        rv_thoughts.adapter = ThoughtsRVAdapter(response.items, this, this)
        Log.v(TAG, response!!.toString())
    }

    override fun onThoughtClicked(thought: Thought) {
        ThoughtActivity.launch(this, thought)
    }

    fun addTagChip(text: String) {
        var chip = Chip(this)
        chip.text = text.trim()
        chip.setTextColor(Color.WHITE)
        chip.chipBackgroundColor = resources.getColorStateList(R.color.tag_chip_bg, theme)
        chipgroup_search.addView(chip)
        txt_search.text.clear()
    }

    fun clearTags() {
        chipgroup_search.removeAllViews()
    }

    override fun onGotTags(tags: ArrayList<TextTagger.GlobalTag>?) {
        Log.v(TAG, "got tags " + tags)

        if (tags != null) {
            for (tag in tags) {
                addTagChip(tag.text)
            }
        }
    }

    fun checkShareIntent() {
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    try {
                        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                            Log.v(TAG, "parsing $it")
                            var str = it
                            txt_search.setText(str)
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }
}