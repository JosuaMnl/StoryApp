package com.yosha10.storyapp.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yosha10.storyapp.auth.login.LoginActivity
import com.yosha10.storyapp.auth.login.dataStore
import com.yosha10.storyapp.auth.register.RegisterActivity
import com.yosha10.storyapp.databinding.ActivityMainBinding
import com.yosha10.storyapp.helper.ViewModelFactory
import com.yosha10.storyapp.pref.StoryPreference
import com.yosha10.storyapp.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {
    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding
    private var viewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupButton()
        setupAnimation()

        init()

        viewModel?.getToken()?.observe(this){ token ->
            if (token != null){
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    private fun init(){
        val pref = StoryPreference.getInstance(dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(pref, this)
        val viewModel: MainViewModel by viewModels {
            factory
        }
        this.viewModel = viewModel
    }

    private fun setupBinding(){
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun setupButton(){
        binding?.btnLogin?.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        binding?.btnRegister?.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }
    }

    private fun setupAnimation(){
        ObjectAnimator.ofFloat(binding?.image, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding?.titleMain, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding?.descriptionMain, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding?.btnRegister, View.ALPHA, 1f).setDuration(500)

        val playTogether = AnimatorSet().apply {
            playTogether(btnLogin, btnRegister)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, playTogether)
            startDelay = 500
            start()
        }
    }
}
