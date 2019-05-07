package com.burningaltar.abce

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.burningaltar.abcelib.UI.TagsTextView
import com.example.brianherbert.trystuff.ce.Thought
import com.example.brianherbert.trystuff.ce.ThoughtContentManager

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

            mImg.setImageResource(thought.thinkerId.defaultIcon)

            var thoughtContent = ThoughtContentManager.getThoughtContent(thought)

            var thoughtTxt = thoughtContent?.getTitleText()
            mDesc.text = thoughtTxt

            var tags = ArrayList<String>()
            for (tag in thought.tags) {
                tags.add(tag.value)
            }

            mLblSpans.setTags(tags, tagsListener, 7)


        }
    }
}