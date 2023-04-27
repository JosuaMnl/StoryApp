package com.yosha10.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yosha10.storyapp.pref.StoryPreference

class MainViewModel(private val pref: StoryPreference): ViewModel() {
    fun getToken(): LiveData<String?>{
        return pref.getToken().asLiveData()
    }
}