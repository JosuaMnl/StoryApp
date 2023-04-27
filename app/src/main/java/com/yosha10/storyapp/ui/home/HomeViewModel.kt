package com.yosha10.storyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yosha10.storyapp.pref.StoryPreference
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository,
                    private val pref: StoryPreference): ViewModel() {
    fun getAllStory() = homeRepository.getAllStory()

    fun clearToken(){
        viewModelScope.launch {
            pref.clearToken()
        }
    }
}