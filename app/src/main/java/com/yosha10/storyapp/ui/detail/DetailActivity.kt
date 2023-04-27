package com.yosha10.storyapp.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yosha10.storyapp.R
import com.yosha10.storyapp.data.local.StoryEntity
import com.yosha10.storyapp.databinding.ActivityDetailBinding
import com.yosha10.storyapp.helper.DateFormatter

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private var _activityDetailBinding: ActivityDetailBinding? = null
    private val binding get() = _activityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupGetDetailStory()
        setupActionBar(getString(R.string.detail_page))
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityDetailBinding = null
    }

    @SuppressLint("SetTextI18n")
    private fun setupGetDetailStory(){
        val story = intent.getParcelableExtra(EXTRA_STORY) as StoryEntity?
        binding?.apply {
            tvDetailName.text = story?.name
            tvDetailCreatedAt.text = "Created At: ${DateFormatter.formatDate(story?.createdAt.toString())}"
            tvDetailDescription.text = story?.description
            Glide.with(this@DetailActivity)
                .load(story?.photoUrl)
                .into(ivDetailPhoto)
            btnShare.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    val name = story?.name
                    val createdAt = "Created At: ${DateFormatter.formatDate(story?.createdAt.toString())}"
                    val desc = story?.description
                    putExtra(Intent.EXTRA_TEXT, "$name\n$createdAt\n\nDeskripsi :\n$desc")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "MyStory")
                startActivity(shareIntent)
            }
        }

    }

    private fun setupActionBar(title: String?){
        supportActionBar?.apply {
            this.title = title
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
        }
    }

    private fun setupBinding(){
        _activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}