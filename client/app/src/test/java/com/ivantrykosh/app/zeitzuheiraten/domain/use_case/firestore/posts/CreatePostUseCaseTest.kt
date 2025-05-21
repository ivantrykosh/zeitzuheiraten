package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.net.Uri
import com.ivantrykosh.app.zeitzuheiraten.data.repository.FirebaseStorageRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.PostRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CreatePostUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var postRepositoryImpl: PostRepositoryImpl
    private lateinit var firebaseStorageRepositoryImpl: FirebaseStorageRepositoryImpl
    private lateinit var createPostUseCase: CreatePostUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        userRepositoryImpl = mock()
        postRepositoryImpl = mock()
        firebaseStorageRepositoryImpl = mock()
        createPostUseCase = CreatePostUseCase(userAuthRepositoryImpl, userRepositoryImpl, postRepositoryImpl, firebaseStorageRepositoryImpl)
    }

    @Test
    fun `create post successfully`() = runBlocking {
        val category = "Video"
        val cities = listOf("Dnipro")
        val minPrice = 10000
        val description = "test"
        val notAvailableDates = emptyList<DatePair>()
        val images = listOf(mock<Uri>())
        val userId = "t1e2s3t4"
        val user = User(name = "Test User", email = "test@email.com")
        val imageUrl = "imageUrl"
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(userRepositoryImpl.getUserById(userId)).doReturn(user)
        whenever(firebaseStorageRepositoryImpl.uploadImage(any(), any())).doReturn(imageUrl)
        whenever(postRepositoryImpl.createPost(any<PostWithRating>())).doReturn(Unit)

        createPostUseCase(category, cities, minPrice, description, notAvailableDates, images).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(userRepositoryImpl).getUserById(userId)
        verify(firebaseStorageRepositoryImpl).uploadImage(any(), any())
        verify(postRepositoryImpl).createPost(any<PostWithRating>())
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `create post first emit must be loading`() = runBlocking {
        val category = "Video"
        val cities = listOf("Dnipro")
        val minPrice = 10000
        val description = "test"
        val notAvailableDates = emptyList<DatePair>()
        val images = listOf(Uri.EMPTY)
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn("")

        createPostUseCase(category, cities, minPrice, description, notAvailableDates, images).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}