package com.yosha10.storyapp.auth.register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yosha10.storyapp.R
import com.yosha10.storyapp.api.ApiConfig
import com.yosha10.storyapp.helper.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel(private val context: Context): ViewModel() {
    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> get() = _isLoading

    private val _statusRegister = MutableLiveData<Boolean>()
    val statusRegister: LiveData<Boolean> get() = _statusRegister

    fun postRegister(name: String, email: String, password: String){
        _isLoading.value = Event(true)
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            try {
                val response = ApiConfig.getApiService().postRegister(name, email, password)
                _isLoading.postValue(Event(false))
                _statusRegister.postValue(true)
                if (!response.error) {
                    _errorMessage.postValue(Event(context.getString(R.string.register_success)))
                } else {
                    _errorMessage.postValue(Event(context.getString(R.string.register_failed)))
                }

            } catch (e: Exception){
                _isLoading.postValue(Event(false))
                _statusRegister.postValue(false)
                val message = e.message.toString() + " | " + context.getString(R.string.register_exception)
                _errorMessage.postValue(Event(message))
            }
        }
    }
}