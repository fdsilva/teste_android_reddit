package com.fastnews.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.fastnews.R
import com.fastnews.service.NetworkState
import com.fastnews.service.model.PostData
import kotlinx.android.synthetic.main.include_item_timeline_thumbnail.view.*

class TimelineAdapter(val onClickItem: (PostData, ImageView) -> Unit) :
    PagedListAdapter<PostData, TimelineItemViewHolder>(PostDiffUtilCallback) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineItemViewHolder
            = TimelineItemViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_timeline,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: TimelineItemViewHolder, position: Int) {
        holder.data = getItem(position)
        holder.view.setOnClickListener { getItem(position)?.let { it1 ->
                onClickItem(it1, holder.view.item_timeline_thumbnail)
            }
        }
    }

//    override fun getItemCount(): Int {
//        this.onClickItem.whenListIsUpdated(super.getItemCount(), this.networkState)
//        return super.getItemCount()
//    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.SUCCESS

    companion object {
        val PostDiffUtilCallback = object : DiffUtil.ItemCallback<PostData>() {
            override fun areItemsTheSame(oldItem: PostData, newItem: PostData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PostData, newItem: PostData): Boolean {
                return oldItem == newItem
            }
        }
    }
}