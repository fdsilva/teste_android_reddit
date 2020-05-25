package com.fastnews.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fastnews.datasource.TimelineDataSourceFactory
import com.fastnews.service.NetworkState
import com.fastnews.service.model.PostData
import kotlinx.coroutines.cancel

private const val pageSize = 10

class PostViewModel() : ViewModel() {
    private val ioScope = viewModelScope
    private val timelineDataSourceFactory by lazy { TimelineDataSourceFactory(scope = ioScope) }
    private val pagedListConfig by lazy {
        PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
    }

    var posts: LiveData<PagedList<PostData>>

    init {
        posts = LivePagedListBuilder(timelineDataSourceFactory, pagedListConfig).build()
    }

    fun verifyConnectionState() {
    }

    override fun onCleared() {
        super.onCleared()
        ioScope.coroutineContext.cancel()
    }

    val networkState: LiveData<NetworkState> = switchMap(
        timelineDataSourceFactory.dataSourceLiveData) {
        it.getNetworkState()
    }
}