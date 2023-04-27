package com.yosha10.storyapp.helper

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yosha10.storyapp.auth.login.LoginViewModel
import com.yosha10.storyapp.auth.register.RegisterViewModel
import com.yosha10.storyapp.di.Injection
import com.yosha10.storyapp.pref.StoryPreference
import com.yosha10.storyapp.ui.add.AddViewModel
import com.yosha10.storyapp.ui.home.HomeRepository
import com.yosha10.storyapp.ui.home.HomeViewModel
import com.yosha10.storyapp.ui.main.MainViewModel

class ViewModelFactory private constructor(
        private val pref: StoryPreference,
        private val homeRepository: HomeRepository,
        private val context: Context
    ): ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(pref) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(pref) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(homeRepository, pref) as T
            modelClass.isAssignableFrom(AddViewModel::class.java) ->
                AddViewModel(context) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(context) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(pref: StoryPreference, context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(pref, Injection.provideRepository(context), context)
            }.also { instance = it }
    }
}