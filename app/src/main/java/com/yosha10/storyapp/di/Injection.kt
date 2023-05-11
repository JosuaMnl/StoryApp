package com.yosha10.storyapp.di

import android.content.Context
import com.yosha10.storyapp.api.ApiConfig
import com.yosha10.storyapp.auth.login.dataStore
import com.yosha10.storyapp.data.local.StoryDatabase
import com.yosha10.storyapp.pref.StoryPreference
import com.yosha10.storyapp.ui.home.HomeRepository

object Injection {
    fun provideRepository(context: Context) : HomeRepository{
        val apiService = ApiConfig.getApiServiceWithHeaders(context)
        val database = StoryDatabase.getInstance(context)
        val pref = StoryPreference.getInstance(context.dataStore)
        return HomeRepository.getInstance(apiService, database, pref)
    }
}