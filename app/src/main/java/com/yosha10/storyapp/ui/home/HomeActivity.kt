package com.yosha10.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yosha10.storyapp.R
import com.yosha10.storyapp.auth.login.dataStore
import com.yosha10.storyapp.databinding.ActivityHomeBinding
import com.yosha10.storyapp.helper.Result
import com.yosha10.storyapp.helper.ViewModelFactory
import com.yosha10.storyapp.pref.StoryPreference
import com.yosha10.storyapp.ui.adapter.StoryAdapter
import com.yosha10.storyapp.ui.add.AddActivity

class HomeActivity : AppCompatActivity() {
    private var _activityHomeBinding: ActivityHomeBinding? = null
    private val binding get() = _activityHomeBinding
    private var viewModel: HomeViewModel? = null

    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        init()
        setupAdapter()
        setupGetAllStory()
        btnAddStory()
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityHomeBinding = null
    }

    override fun onResume() {
        super.onResume()
        setupGetAllStory()
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_language -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            R.id.action_logout -> {
                viewModel?.clearToken()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAdapter() {
        storyAdapter = StoryAdapter()
        binding?.rvStory?.apply {
            this.layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = storyAdapter
        }
    }

    private fun init() {
        val pref = StoryPreference.getInstance(dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(pref, applicationContext)
        val viewModel: HomeViewModel by viewModels {
            factory
        }
        this.viewModel = viewModel
    }

    private fun setupGetAllStory() {
        viewModel?.getAllStory()?.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> binding?.loading?.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding?.loading?.visibility = View.GONE
                        val storyData = result.data
                        storyAdapter.submitList(storyData)
                    }
                    is Result.Error -> {
                        binding?.loading?.visibility = View.GONE
                        result.error.getContentIfNotHandled()?.let { msg ->
                            Snackbar.make(window.decorView, msg, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupBinding() {
        _activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun btnAddStory() {
        binding?.fabAdd?.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddActivity::class.java))
        }
    }
}