package com.yosha10.storyapp.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.snackbar.Snackbar
import com.yosha10.storyapp.R
import com.yosha10.storyapp.databinding.ActivityLoginBinding
import com.yosha10.storyapp.helper.ViewModelFactory
import com.yosha10.storyapp.pref.StoryPreference
import com.yosha10.storyapp.ui.home.HomeActivity

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name="settings")
class LoginActivity : AppCompatActivity() {
    private var _activityLoginBinding: ActivityLoginBinding? = null
    private val binding get() = _activityLoginBinding
    private var viewModel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()

        init()
        setupObserve()
        setupButtonLogin()
        setupAnimation()
        setupActionBar("Login StoryApp")
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityLoginBinding = null
    }

    private fun postLogin(){
        val email = binding?.edLoginEmail?.text.toString()
        val password = binding?.edLoginPassword?.text.toString()
        when {
            email.isEmpty() -> binding?.layoutLoginEmail?.error = getString(R.string.field_empty)
            password.isEmpty() -> binding?.layoutLoginPassword?.error = getString(R.string.field_empty)
            else -> viewModel?.postLogin(email, password)
        }
    }

    private fun init(){
        val pref = StoryPreference.getInstance(dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(pref, this)
        val viewModel: LoginViewModel by viewModels {
            factory
        }
        this.viewModel = viewModel
    }

    private fun setupObserve(){
        viewModel?.isLoading?.observe(this){
            it.getContentIfNotHandled()?.let { isLoading ->
                binding?.loading?.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewModel?.statusLogin?.observe(this){
            it.getContentIfNotHandled()?.let { success ->
                val msg: String?
                if (success) {
                    msg = getString(R.string.login_success)
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                } else {
                    msg = getString(R.string.login_failed)
                }
                Snackbar.make(window.decorView.rootView, msg, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel?.tokenLogin?.observe(this){ token ->
            if (token != null) {
                saveToken(token)
            }
        }
    }

    private fun setupButtonLogin(){
        binding?.btnSignin?.setOnClickListener {
            postLogin()
        }
    }

    private fun setupAnimation(){
        ObjectAnimator.ofFloat(binding?.imageLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding?.titleLogin, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding?.descriptionLogin, View.ALPHA, 1f).setDuration(500)
        val txtEmail = ObjectAnimator.ofFloat(binding?.txtLoginEmail, View.ALPHA, 1f).setDuration(500)
        val txtPassword = ObjectAnimator.ofFloat(binding?.txtLoginPassword, View.ALPHA, 1f).setDuration(500)
        val layoutEdEmail = ObjectAnimator.ofFloat(binding?.layoutLoginEmail, View.ALPHA, 1f).setDuration(500)
        val layoutEdPassword = ObjectAnimator.ofFloat(binding?.layoutLoginPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding?.btnSignin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, desc, txtEmail, layoutEdEmail, txtPassword, layoutEdPassword, btnLogin)
            startDelay = 500
            start()
        }
    }

    private fun saveToken(token: String){
        viewModel?.saveToken(token)
    }

    private fun setupBinding(){
        _activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun setupActionBar(title: String?){
        supportActionBar?.apply {
            this.title = title
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
        }
    }
}