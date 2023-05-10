package com.yosha10.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.*
import com.yosha10.storyapp.api.ApiService
import com.yosha10.storyapp.data.local.StoryDao
import com.yosha10.storyapp.data.local.StoryDatabase
import com.yosha10.storyapp.data.local.StoryEntity
import com.yosha10.storyapp.data.paging.StoryRemoteMediator
import com.yosha10.storyapp.helper.Event
import com.yosha10.storyapp.helper.Result

class HomeRepository private constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
){
//    private val _loadingState = MutableLiveData<Event<Boolean>>()
//    val loadingState: LiveData<Event<Boolean>> get() =  _loadingState

    @OptIn(ExperimentalPagingApi::class)
    fun getStory(): LiveData<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var INSTANCE: HomeRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): HomeRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HomeRepository(apiService, storyDatabase)
            }.also { INSTANCE = it }
    }
}