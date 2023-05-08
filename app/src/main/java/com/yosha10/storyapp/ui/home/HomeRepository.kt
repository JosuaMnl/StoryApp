package com.yosha10.storyapp.ui.home

import androidx.lifecycle.LiveData
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
//    private val storyDao: StoryDao
    private val storyDatabase: StoryDatabase
){
//    fun getAllStory(): LiveData<Result<List<StoryEntity>>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.getAllStory()
//            val story = response.listStory
//            if (story != null) {
//                val storyList = story.map { story ->
//                    StoryEntity(
//                        story.id,
//                        story.name,
//                        story.description,
//                        story.photoUrl,
//                        story.createdAt,
//                        story.lat,
//                        story.lon
//                    )
//                }
//                storyDao.insertStory(storyList)
//            } else {
//                emit(Result.Error(Event("Data Story Kosong!")))
//            }
//        } catch (e: Exception){
//            emit(Result.Error(Event(e.message.toString())))
//        }
//        val localData: LiveData<Result<List<StoryEntity>>> = storyDao.getStory().map { listStory ->
//            Result.Success(listStory)
//        }
//        emitSource(localData)
//    }

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