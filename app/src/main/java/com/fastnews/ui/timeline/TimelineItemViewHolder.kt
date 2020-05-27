package com.fastnews.ui.timeline

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fastnews.R
import com.fastnews.mechanism.TimeElapsed
import com.fastnews.service.model.PostData
import kotlinx.android.synthetic.main.include_item_timeline_ic_comments.view.*
import kotlinx.android.synthetic.main.include_item_timeline_ic_score.view.*
import kotlinx.android.synthetic.main.include_item_timeline_thumbnail.view.*
import kotlinx.android.synthetic.main.include_item_timeline_timeleft.view.*
import kotlinx.android.synthetic.main.include_item_timeline_title.view.*

class TimelineItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var data: PostData? = null
        set(value) {
            field = value
            populateAuthor(value)
            populateTime(value)
            populateThumbnail(value)
            populateTitle(value)
            populateScore(value)
            populateComments(value)
        }

    private fun populateComments(value: PostData?) {
        value?.num_comments.let {
            view.item_timeline_bt_comments_text.text = it.toString()
        }
    }

    private fun populateScore(value: PostData?) {
        value?.score.let {
            view.item_timeline_bt_score_text.text = it.toString()
        }
    }

    private fun populateTitle(value: PostData?) {
        value?.title.let {
            view.item_timeline_title.text = it
        }
    }

    private fun populateThumbnail(value: PostData?) {
        var imageURL = ""
        if (value?.preview != null) {
            value.preview.images.map {
                if (!TextUtils.isEmpty(it.source.url)) {
                    imageURL = HtmlCompat.fromHtml(it.source.url, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                }
            }
        }
        if (!TextUtils.isEmpty(imageURL)) {
            Glide.with(view.item_timeline_thumbnail.context)
                .load(imageURL)
                .placeholder(R.drawable.ic_placeholder)
                .error(ColorDrawable(Color.DKGRAY))
                .into(view.item_timeline_thumbnail)
            view.item_timeline_thumbnail.visibility = View.VISIBLE
        } else {
            view.item_timeline_thumbnail.visibility = View.GONE
        }
    }

    private fun populateTime(value: PostData?) {
        value?.created_utc.let {
            val elapsed = TimeElapsed.getTimeElapsed(it!!)
            view.item_timeline_timeleft.text = elapsed
        }
    }

    private fun populateAuthor(value: PostData?) {
        value?.author.let {
            view.item_timeline_author.text = it
        }
    }

    companion object {
        fun create(parent: ViewGroup) : TimelineItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_timeline, parent, false)
            return TimelineItemViewHolder(view)
        }
    }
}