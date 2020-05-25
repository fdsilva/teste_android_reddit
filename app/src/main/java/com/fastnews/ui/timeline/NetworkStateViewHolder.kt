package com.fastnews.ui.timeline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fastnews.R
import com.fastnews.service.NetworkState
import kotlinx.android.synthetic.main.item_network_state.view.*

class NetworkStateViewHolder (val view: View, private val retryCallback: () -> Unit) :
    RecyclerView.ViewHolder(view) {

    init {
        itemView.retryButton.setOnClickListener { retryCallback() }
    }

    fun bindTo(networkState: NetworkState) {
        itemView.errorMessageTextView.visibility = if (networkState == NetworkState.FAILED) View.VISIBLE else View.GONE
        itemView.retryButton.visibility = if (networkState == NetworkState.FAILED) View.VISIBLE else View.GONE
        itemView.loadingProgressBar.visibility = if (networkState == NetworkState.RUNNING) View.VISIBLE else View.GONE
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_network_state, parent, false)
            return NetworkStateViewHolder(view, retryCallback)
        }
    }
}