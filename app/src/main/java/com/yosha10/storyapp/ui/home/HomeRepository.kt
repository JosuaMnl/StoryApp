package com.yosha10.storyapp.ui.home

import androidx.lifecycle.*
import androidx.paging.*
import com.yosha10.storyapp.api.ApiService
import com.yosha10.storyapp.data.local.StoryDao
import com.yosha10.storyapp.data.local.StoryDatabase
import com.yosha10.storyapp.data.local.StoryEntity
import com.yosha10.storyapp.data.paging.StoryRemoteMediator
import com.yosha10.storyapp.helper.Event
import com.yosha10.storyapp.helper.Result
import com.yosha10.storyapp.pref.StoryPreference
import kotlinx.coroutines.launch

class HomeRepository private constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val pref: StoryPreference
) {
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

    suspend fun clearToken() {
        pref.clearToken()
    }

    companion object {
        @Volatile
        private var INSTANCE: HomeRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase,
            pref: StoryPreference
        ): HomeRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HomeRepository(apiService, storyDatabase, pref)
            }.also { INSTANCE = it }
    }
}