package com.yosha10.storyapp.data.paging

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yosha10.storyapp.api.ApiService
import com.yosha10.storyapp.data.*
import com.yosha10.storyapp.data.local.StoryDatabase
import com.yosha10.storyapp.data.local.StoryEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {
    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi
        )
        val pagingState = PagingState<Int, StoryEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun postRegister(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        TODO("Not yet implemented")
    }

    override suspend fun postLogin(email: String, password: String): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getAllStory(page: Int, size: Int, location: Int): StoryResponse {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
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
        return StoryResponse(
            false,
            "success",
            items.subList((page - 1) * size, (page - 1) * size + size)
        )
    }

    override suspend fun addStory(
        file: MultipartBody.Part,
        description: RequestBody
    ): AddStoryResponse {
        TODO("Not yet implemented")
    }
}