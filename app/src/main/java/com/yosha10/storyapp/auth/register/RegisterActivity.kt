package com.yosha10.storyapp.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.yosha10.storyapp.R
import com.yosha10.storyapp.auth.login.dataStore
import com.yosha10.storyapp.databinding.ActivityRegisterBinding
import com.yosha10.storyapp.helper.ViewModelFactory
import com.yosha10.storyapp.pref.StoryPreference

class RegisterActivity : AppCompatActivity() {
    private var _activityRegisterBinding: ActivityRegisterBinding? = null
    private val binding get() = _activityRegisterBinding

    private var viewModel: RegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        init()
        setupObserve()
        setupAnimation()
        setupActionBar("Register StoryApp")

        binding?.btnSignup?.setOnClickListener {
            postRegister()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityRegisterBinding = null
    }

    private fun init(){
        val pref = StoryPreference.getInstance(dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(pref, applicationContext)
        val viewModel: RegisterViewModel by viewModels {
            factory
        }
        this.viewModel = viewModel
    }

    private fun setupObserve(){
        viewModel?.errorMessage?.observe(this){
            it.getContentIfNotHandled().let { msg ->
                if (msg != null) {
                    Snackbar.make(window.decorView.rootView,msg,Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        viewModel?.statusRegister?.observe(this){
            if (it) {
                setEmpty()
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 1000)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 1000)
            }
        }

        viewModel?.isLoading?.observe(this){
            it.getContentIfNotHandled()?.let { isLoading ->
                binding?.loading?.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun postRegister(){
        val name = binding?.edRegisterName?.text.toString()
        val email = binding?.edRegisterEmail?.text.toString()
        val password = binding?.edRegisterPassword?.text.toString()
        when {
            name.isEmpty() -> binding?.layoutRegisterName?.error = getString(R.string.field_empty)
            email.isEmpty() -> binding?.layoutRegisterEmail?.error = getString(R.string.field_empty)
            password.isEmpty() -> binding?.layoutRegisterPassword?.error = getString(R.string.field_empty)
            else -> viewModel?.postRegister(name, email, password)
        }
    }

    private fun setEmpty(){
        binding?.edRegisterName?.text = null
        binding?.edRegisterEmail?.text = null
        binding?.edRegisterPassword?.text = null
    }

    private fun setupBinding(){
        _activityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun setupAnimation(){
        ObjectAnimator.ofFloat(binding?.imageRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding?.titleRegister, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding?.descriptionRegister, View.ALPHA, 1f).setDuration(500)
        val txtRegisterName = ObjectAnimator.ofFloat(binding?.txtRegisterName, View.ALPHA, 1f).setDuration(500)
        val txtRegisterEmail = ObjectAnimator.ofFloat(binding?.txtRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val txtRegisterPassword = ObjectAnimator.ofFloat(binding?.txtRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val layoutEdName = ObjectAnimator.ofFloat(binding?.layoutRegisterName, View.ALPHA, 1f).setDuration(500)
        val layoutEdEmail = ObjectAnimator.ofFloat(binding?.layoutRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val layoutEdPassword = ObjectAnimator.ofFloat(binding?.layoutRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding?.btnSignup, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, desc, txtRegisterName, layoutEdName, txtRegisterEmail,
                layoutEdEmail, txtRegisterPassword, layoutEdPassword, btnRegister)
            startDelay = 500
            start()
        }
    }

    private fun setupActionBar(title: String?){
        supportActionBar?.apply {
            this.title = title
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
        }
    }
}