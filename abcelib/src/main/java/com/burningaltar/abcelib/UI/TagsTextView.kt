package com.burningaltar.abcelib.UI

import android.content.Context
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView

class TagsTextView: TextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    interface TagClickedListener {
        fun onTagClicked(tag : String)
    }

    fun setTags(tags : List<String>, listener: TagClickedListener?, maxTags : Int = 0) {
        movementMethod = LinkMovementMethod.getInstance()

        hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE

        var strBuilder = SpannableStringBuilder()
        var isFirst = true

        var idx = 0
        for (tag in tags) {
            if (!isFirst) {
                strBuilder.append(" ")
            }
            isFirst = false
            strBuilder.append(tag)
            var span = LinkSpan(tag, listener)
            var start = strBuilder.length - tag.length
            var end = strBuilder.length - tag.length + tag.length

            var str = strBuilder.toString().subSequence(start, end)

            strBuilder.setSpan(span, strBuilder.length - tag.length, strBuilder.length - tag.length + tag.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            idx++

            if (maxTags in 1..idx) {
                strBuilder.append("...")
                break
            }
        }

        text = strBuilder
    }

    class LinkSpan constructor(val str: String, val listener: TagClickedListener?) : ClickableSpan() {
        override fun onClick(view: View) {
            listener?.onTagClicked(str)
        }
    }
}