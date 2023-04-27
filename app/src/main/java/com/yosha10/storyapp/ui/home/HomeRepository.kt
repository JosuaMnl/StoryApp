package com.yosha10.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.yosha10.storyapp.api.ApiService
import com.yosha10.storyapp.data.local.StoryDao
import com.yosha10.storyapp.data.local.StoryEntity
import com.yosha10.storyapp.helper.Event
import com.yosha10.storyapp.helper.Result

class HomeRepository private constructor(
    private val apiService: ApiService,
    private val storyDao: StoryDao
){
    fun getAllStory(): LiveData<Result<List<StoryEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStory()
            val story = response.listStory
            if (story != null) {
                val storyList = story.map { story ->
                    StoryEntity(
                        story.id,
                        story.name,
                        story.description,
                        story.photoUrl,
                        story.createdAt
                    )
                }
                storyDao.insertStory(storyList)
            } else {
                emit(Result.Error(Event("Data Story Kosong!")))
            }
        } catch (e: Exception){
            emit(Result.Error(Event(e.message.toString())))
        }
        val localData: LiveData<Result<List<StoryEntity>>> = storyDao.getStory().map { listStory ->
            Result.Success(listStory)
        }
        emitSource(localData)
    }

    companion object {
        @Volatile
        private var INSTANCE: HomeRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDao: StoryDao
        ): HomeRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HomeRepository(apiService, storyDao)
            }.also { INSTANCE = it }
    }
}