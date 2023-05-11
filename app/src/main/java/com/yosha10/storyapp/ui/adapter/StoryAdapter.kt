package com.yosha10.storyapp.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yosha10.storyapp.R
import com.yosha10.storyapp.data.local.StoryEntity
import com.yosha10.storyapp.databinding.ItemStoryBinding
import com.yosha10.storyapp.helper.DateFormatter
import com.yosha10.storyapp.ui.adapter.StoryAdapter.MyViewHolder
import com.yosha10.storyapp.ui.detail.DetailActivity

class StoryAdapter: PagingDataAdapter<StoryEntity, MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class MyViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(story: StoryEntity){
            binding.tvItemName.text = story.name
            binding.tvItemCreatedAt.text = DateFormatter.formatDate(story.createdAt)
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(binding.ivItemPhoto)
            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STORY, story)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto, "picture"),
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.tvItemCreatedAt, "createdAt")
                    )
                it.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>() {
                override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}