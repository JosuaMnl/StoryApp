package com.yosha10.storyapp.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService

class StoryAppWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory =
        StackRemoteViewsFactory(this.applicationContext)
}