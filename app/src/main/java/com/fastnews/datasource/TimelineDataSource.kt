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
    private val handler = CoroutineExceptionHandler { _, _ ->
        networkState.postValue(NetworkState.FAILED)
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<PostData>
    ) {
        getPostList("", params.requestedLoadSize) {
            callback.onResult(it)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<PostData>) {
        getPostList(params.key, params.requestedLoadSize) {
            callback.onResult(it)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PostData>) {}

    override fun getKey(item: PostData): String {
        return item.name
    }

    private fun getPostList(after: String, size: Int, callback: (List<PostData>) -> Unit) {
        networkState.postValue(NetworkState.RUNNING)
        scope.launch (handler + supervisorJob) {
            val posts = PostRepository.getPosts(after, size)
            networkState.postValue(NetworkState.SUCCESS)
            callback(posts)
        }
    }

    fun getNetworkState(): LiveData<NetworkState> = networkState
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
}