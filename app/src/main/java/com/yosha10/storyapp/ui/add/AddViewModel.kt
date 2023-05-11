package com.yosha10.storyapp.ui.add

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yosha10.storyapp.R
import com.yosha10.storyapp.api.ApiConfig
import com.yosha10.storyapp.helper.Event
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddViewModel(private val context: Context): ViewModel() {
    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> get() = _isLoading

    private val _statusAddStory = MutableLiveData<Boolean>()
    val statusAddStory: LiveData<Boolean> get() = _statusAddStory

    private val _imagePreview = MutableLiveData<String>()
    val imagePreview: LiveData<String> get() = _imagePreview

    private val _fileImage = MutableLiveData<File>()
    val fileImage: LiveData<File> get() = _fileImage

    fun setImagePreview(image: String){
        _imagePreview.value = image
    }

    fun setFileImage(file: File){
        _fileImage.value = file
    }

    fun postStory(image: MultipartBody.Part, description: RequestBody, lat: Double, lon: Double){
        _isLoading.value = Event(true)
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiServiceWithHeaders(context)
                val uploadImageRequest = apiService.addStory(image, description, lat, lon)
                _isLoading.postValue(Event(false))
                val error = uploadImageRequest.error
                val message = uploadImageRequest.message
                if (error){
                    _statusAddStory.postValue(false)
                    _errorMessage.postValue(Event(context.getString(R.string.upload_failed) + message))
                } else {
                    _statusAddStory.postValue(true)
                    _errorMessage.postValue(Event(context.getString(R.string.upload_success) + message))
                }
            } catch (e: Exception) {
                _isLoading.postValue(Event(false))
                _statusAddStory.postValue(false)
                _errorMessage.postValue(Event(e.message.toString()))
            }
        }
    }
}