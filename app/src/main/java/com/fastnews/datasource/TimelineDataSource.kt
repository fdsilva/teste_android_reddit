package com.fastnews.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.fastnews.repository.PostRepository
import com.fastnews.service.NetworkState
import com.fastnews.service.model.PostData
import kotlinx.coroutines.*

class TimelineDataSource(private val scope: CoroutineScope)
    : ItemKeyedDataSource<String, PostData>() {

    private var supervisorJob = SupervisorJob()
    private val networkState = MutableLiveData<NetworkState>()
    private val initialLoadNetworkState = MutableLiveData<NetworkState>()

    private val initialLoadHandler = CoroutineExceptionHandler { _, _ ->
        initialLoadNetworkState.postValue(NetworkState.FAILED) }

    private val handler = CoroutineExceptionHandler { _, _ ->
        networkState.postValue(NetworkState.FAILED) }

    private var retry: (() -> Any)? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<PostData>) {
        retry = {loadInitial(params, callback)}
        initialLoadNetworkState.postValue(NetworkState.RUNNING)

        scope.launch (initialLoadHandler + supervisorJob) {
            val posts = PostRepository.getPosts("", params.requestedLoadSize)
            initialLoadNetworkState.postValue(NetworkState.SUCCESS)
            callback.onResult(posts)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<PostData>) {
        retry = {loadAfter(params, callback)}
        networkState.postValue(NetworkState.RUNNING)

        scope.launch (handler + supervisorJob) {
            val posts = PostRepository.getPosts(params.key, params.requestedLoadSize)
            networkState.postValue(NetworkState.SUCCESS)
            callback.onResult(posts)
        }
    }

    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PostData>) {}

    override fun getKey(item: PostData): String {
        return item.name
    }

    fun getInitialLoadNetworkState(): LiveData<NetworkState> = initialLoadNetworkState

    fun getNetworkState(): LiveData<NetworkState> = networkState

    fun refreshAll() = this.invalidate()

    fun doRetry() { retry?.invoke() }
}

class TimelineDataSourceFactory(
    private val scope: CoroutineScope)
    : DataSource.Factory<String, PostData>() {

    val dataSourceLiveData = MutableLiveData<TimelineDataSource>()

    override fun create(): DataSource<String, PostData> {
        val timelineDataSource = TimelineDataSource(scope)
        dataSourceLiveData.postValue(timelineDataSource)
       return timelineDataSource
    }

    fun getSourceValue() = dataSourceLiveData.value
}