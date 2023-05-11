package com.yosha10.storyapp.utils

import com.yosha10.storyapp.data.local.StoryEntity

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0 .. 100) {
            val story = StoryEntity(
                i.toString(),
                "name + $i",
                "desc + $i",
                "photoUrl + $i",
                "createdAt + $i",
                1.1,
                1.1,
            )
            items.add(story)
        }
        return items
    }
}