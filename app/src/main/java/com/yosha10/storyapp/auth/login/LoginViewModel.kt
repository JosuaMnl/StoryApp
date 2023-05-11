package com.yosha10.storyapp.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yosha10.storyapp.api.ApiConfig
import com.yosha10.storyapp.helper.Event
import com.yosha10.storyapp.pref.StoryPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: StoryPreference) : ViewModel() {
    private val _tokenLogin = MutableLiveData<String>()
    val tokenLogin: LiveData<String> = _tokenLogin

    private val _statusLogin = MutableLiveData<Event<Boolean>>()
    val statusLogin: LiveData<Event<Boolean>> = _statusLogin

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> get() = _isLoading

    fun postLogin(email: String, password: String) {
        _isLoading.value = Event(true)
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            try {
                val response = ApiConfig.getApiService().postLogin(email, password)
                _isLoading.postValue(Event(false))
                val error = response.error
                val token = response.loginResult.token
                if (!error) {
                    _tokenLogin.postValue(token)
                    _statusLogin.postValue(Event(true))
                } else {
                    _statusLogin.postValue(Event(false))
                }
            } catch (e: Exception) {
                _isLoading.postValue(Event(false))
                _statusLogin.postValue(Event(false))
            }
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }
}