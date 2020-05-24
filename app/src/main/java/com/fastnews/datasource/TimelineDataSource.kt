package com.fastnews.datasource

import android.util.Log
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.fastnews.repository.PostRepository
import com.fastnews.service.model.PostData
import kotlinx.coroutines.*

class TimelineDataSource(private val scope: CoroutineScope)
    : ItemKeyedDataSource<String, PostData>() {

    private var supervisorJob = SupervisorJob()

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
    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(TimelineDataSource::class.java.simpleName, "An error happened: $e")
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PostData>) {}

    override fun getKey(item: PostData): String {
        return item.name
    }

    private fun getPostList(after: String, size: Int, callback: (List<PostData>) -> Unit) {
        scope.launch (getJobErrorHandler() + supervisorJob){
            val posts = PostRepository.getPosts(after, size)
            callback(posts)
        }
    }
}

class TimelineDataSourceFactory(
    private val scope: CoroutineScope)
    : DataSource.Factory<String, PostData>()
{
    override fun create(): DataSource<String, PostData> = TimelineDataSource(scope)
}