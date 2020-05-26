package com.fastnews.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastnews.mechanism.VerifyNetworkInfo
import com.fastnews.repository.CommentRepository
import com.fastnews.service.NetworkState
import com.fastnews.service.model.CommentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostDetailViewModel(private val networkInfo: VerifyNetworkInfo) : ViewModel() {

    private val _comments = MutableLiveData<List<CommentData>>()
    private val _networkStatus = MutableLiveData<NetworkState>()

    val comments: LiveData<List<CommentData>> = _comments
    val networkStatus = _networkStatus

    fun getComments(postId: String) {
        if(networkInfo.isConnected()) {
            viewModelScope.launch(Dispatchers.IO) {
                _networkStatus.postValue(NetworkState.RUNNING)
                _comments.postValue(CommentRepository.getComments(postId))
                _networkStatus.postValue(NetworkState.SUCCESS)
            }
        } else {
            _networkStatus.postValue(NetworkState.FAILED)
        }
    }
}