package com.yosha10.storyapp.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.yosha10.storyapp.R
import com.yosha10.storyapp.data.local.StoryDao
import com.yosha10.storyapp.data.local.StoryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class StackRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {
    private val listImage = ArrayList<Bitmap>()
    private lateinit var storyDao: StoryDao

    override fun onCreate() {
        storyDao = StoryDatabase.getInstance(context).storyDao()
    }

    override fun onDataSetChanged() {
        val identityToken = Binder.clearCallingIdentity()

        runBlocking(Dispatchers.IO) {
            try {
                storyDao.getImageStory().map { data ->
                    val imageBitmap =
                        try {
                            Glide.with(context)
                                .asBitmap()
                                .load(data.photoUrl)
                                .submit()
                                .get()
                        } catch (e: Exception) {
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.ic_image_placeholder
                            )
                        }
                    listImage.add(imageBitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = listImage.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.story_app_widget)
        rv.setImageViewBitmap(R.id.image_widget, listImage[position])

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}