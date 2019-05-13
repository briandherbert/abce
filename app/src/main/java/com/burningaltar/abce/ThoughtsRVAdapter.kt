package com.burningaltar.abce

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.burningaltar.abcelib.UI.TagsTextView
import com.example.brianherbert.biblenavwatch.data.BibleRef
import com.example.brianherbert.biblenavwatch.data.BibleVersion
import com.example.brianherbert.trystuff.ce.Thought
import com.example.brianherbert.trystuff.ce.ThoughtContentManager
import com.example.utils.bl.BibleFetcher
import com.example.utils.bl.YVFetcher
import com.example.utils.data.BiblePassage

class ThoughtsRVAdapter(
    val thoughts: ArrayList<Thought>,
    val thoughtClickedListener: ThoughtClickedListener,
    val tagsListener: TagsTextView.TagClickedListener
) :
    RecyclerView.Adapter<ThoughtsRVAdapter.ThoughtVH>() {

    interface ThoughtClickedListener {
        fun onThoughtClicked(thought: Thought)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThoughtVH {
        return ThoughtVH(LayoutInflater.from(parent.context).inflate(R.layout.thought_item, parent, false))

    }

    override fun getItemCount(): Int {
        return thoughts.size
    }

    override fun onBindViewHolder(holder: ThoughtVH, position: Int) {
        holder.bind(thoughts[position])
    }

    inner class ThoughtVH(val v: View) : RecyclerView.ViewHolder(v) {
        var mImg: ImageView
        var mImgContentType: ImageView
        var mDesc: TextView
        var mLblSpans: TagsTextView

        init {
            mImg = v.findViewById(R.id.img_thought_icon)
            mImgContentType = v.findViewById(R.id.img_content_type_icon)
            mDesc = v.findViewById(R.id.lbl_thought_desc)
            mLblSpans = v.findViewById(R.id.tags_tv)
        }

        fun bind(thought: Thought) {
            v.setOnClickListener { thoughtClickedListener.onThoughtClicked(thought) }

            mImg.setImageResource(thought.cacheId.defaultIcon)

            var thoughtContent = ThoughtContentManager.getThoughtContent(thought)

            var thoughtTxt = thoughtContent?.getTitleText()
            mDesc.text = thoughtTxt

            var tags = ArrayList<String>()
            for (tag in thought.tags) {
                tags.add(tag.value)
            }

            if (thoughtContent?.getYoutubeId() != null) {
                mImgContentType.visibility = View.VISIBLE
                mImgContentType.setImageResource(R.drawable.ic_video)
            } else if (thoughtContent?.getAudioStreamUrl() != null) {
                mImgContentType.visibility = View.VISIBLE
                mImgContentType.setImageResource(R.drawable.ic_audio)
            } else {
                mImgContentType.visibility = View.GONE
            }

            mLblSpans.setTags(tags, tagsListener, 7)

            // Get verse text
            if (thoughtContent is ThoughtContentManager.VersesContent) {
                YVFetcher(mImg.context, object : BibleFetcher.BibleFetcherListener {
                    override fun onFetched(response: BiblePassage?) {
                        mDesc.text = response?.content + " " + response?.getHumanRef()
                    }
                }).getPassage(BibleRef(BibleVersion.ESV, thoughtContent.ref))
            }
        }
    }
}