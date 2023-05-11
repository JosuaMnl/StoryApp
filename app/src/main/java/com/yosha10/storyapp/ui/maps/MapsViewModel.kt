package com.yosha10.storyapp.ui.maps

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yosha10.storyapp.data.local.StoryDatabase
import com.yosha10.storyapp.data.local.StoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel(context: Context): ViewModel() {
    private val storyDatabase: StoryDatabase = StoryDatabase.getInstance(context)

    private val _storyLocationList = MutableLiveData<List<StoryEntity>>()
    val storyLocationList: LiveData<List<StoryEntity>> get() = _storyLocationList
    init {
        setData()
    }

    private fun setData(){
        viewModelScope.launch(Dispatchers.IO){
            val data = storyDatabase.storyDao().getStoryWithLocation()
            _storyLocationList.postValue(data)
        }
    }
}