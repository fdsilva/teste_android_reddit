package com.fastnews.ui.timeline

import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fastnews.R
import com.fastnews.service.NetworkState
import com.fastnews.service.model.PostData
import kotlinx.android.synthetic.main.include_item_timeline_thumbnail.view.*
import java.lang.IllegalArgumentException

class TimelineAdapter(private val retryCallback: () -> Unit, val onClickItem: (PostData, ImageView) -> Unit) :
    PagedListAdapter<PostData, RecyclerView.ViewHolder>(PostDiffUtilCallback) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_timeline -> TimelineItemViewHolder.create(parent)
            R.layout.item_network_state -> NetworkStateViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_timeline -> {
                (holder as TimelineItemViewHolder).data = getItem(position)
                holder.view.setOnClickListener{
                    getItem(position).let { data ->
                        data?.let { it1 -> onClickItem(it1, holder.view.item_timeline_thumbnail) }
                    }
                }
            }
            R.layout.item_network_state -> networkState?.let {
                (holder as NetworkStateViewHolder).bindTo(
                    it
                )
            }
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.SUCCESS

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_network_state
        } else {
            R.layout.item_timeline
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        if (currentList != null) {
            if (currentList!!.size != 0) {
                val previousState = this.networkState
                val hadExtraRow = hasExtraRow()
                this.networkState = newNetworkState
                val hasExtraRow = hasExtraRow()
                if (hadExtraRow != hasExtraRow) {
                    if (hadExtraRow) {
                        notifyItemRemoved(super.getItemCount())
                    } else {
                        notifyItemInserted(super.getItemCount())
                    }
                } else if (hasExtraRow && previousState !== newNetworkState) {
                    notifyItemChanged(itemCount - 1)
                }
            }
        }
    }
    
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