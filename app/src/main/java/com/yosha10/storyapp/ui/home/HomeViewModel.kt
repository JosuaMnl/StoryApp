package com.yosha10.storyapp.ui.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yosha10.storyapp.data.local.StoryEntity
import com.yosha10.storyapp.helper.Event
import com.yosha10.storyapp.pref.StoryPreference
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository): ViewModel()
{

    private val _loadingState = MutableLiveData<Event<Boolean>>()
    val loadingState: LiveData<Event<Boolean>> get() =  _loadingState

    val story: LiveData<PagingData<StoryEntity>> =
        homeRepository.getStory().cachedIn(viewModelScope)

    fun setLoading(isLoading: Boolean){
        _loadingState.value = Event(isLoading)
    }

    fun clearToken(){
        viewModelScope.launch {
            homeRepository.clearToken()
        }
    }

}