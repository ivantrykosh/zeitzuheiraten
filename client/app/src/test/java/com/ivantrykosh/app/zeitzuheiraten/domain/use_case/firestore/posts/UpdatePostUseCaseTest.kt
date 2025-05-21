package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.net.Uri
import com.ivantrykosh.app.zeitzuheiraten.data.repository.FirebaseStorageRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.PostRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.data.repository.UserAuthRepositoryImpl
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
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
class UpdatePostUseCaseTest {

    private lateinit var userAuthRepositoryImpl: UserAuthRepositoryImpl
    private lateinit var postRepositoryImpl: PostRepositoryImpl
    private lateinit var firebaseStorageRepositoryImpl: FirebaseStorageRepositoryImpl
    private lateinit var updatePostUseCase: UpdatePostUseCase

    @Before
    fun setup() {
        userAuthRepositoryImpl = mock()
        postRepositoryImpl = mock()
        firebaseStorageRepositoryImpl = mock()
        updatePostUseCase = UpdatePostUseCase(userAuthRepositoryImpl, postRepositoryImpl, firebaseStorageRepositoryImpl)
    }

    @Test
    fun `update post successfully`() = runBlocking {
        val id = "postId"
        val cities = listOf("Dnipro")
        val minPrice = 10000
        val description = "test"
        val notAvailableDates = emptyList<DatePair>()
        val images = listOf(mock<Uri>())
        val previousImages = listOf("url1", "url2")
        val uploadNewImages = true
        val enabled = true
        val userId = "t1e2s3t4"
        val imageUrl = "imageUrl"
        var resourceSuccess = false
        whenever(userAuthRepositoryImpl.getCurrentUserId()).doReturn(userId)
        whenever(firebaseStorageRepositoryImpl.deleteImage(any())).doReturn(Unit)
        whenever(firebaseStorageRepositoryImpl.uploadImage(any(), any())).doReturn(imageUrl)
        whenever(postRepositoryImpl.updatePost(any<PostWithRating>())).doReturn(Unit)

        updatePostUseCase(id, cities, minPrice, description, notAvailableDates, images, previousImages, uploadNewImages, enabled).collect { result ->
            when (result) {
                is Resource.Loading -> { }
                is Resource.Error -> { Assert.fail(result.error.message) }
                is Resource.Success -> { resourceSuccess = true }
            }
        }

        verify(userAuthRepositoryImpl).getCurrentUserId()
        verify(firebaseStorageRepositoryImpl).deleteImage(any())
        verify(firebaseStorageRepositoryImpl).uploadImage(any(), any())
        verify(postRepositoryImpl).updatePost(any<PostWithRating>())
        Assert.assertTrue(resourceSuccess)
    }

    @Test(expected = CancellationException::class)
    fun `update post first emit must be loading`() = runBlocking {
        val id = "postId"
        val cities = listOf("Dnipro")
        val minPrice = 10000
        val description = "test"
        val notAvailableDates = emptyList<DatePair>()
        val images = listOf(mock<Uri>())
        val previousImages = listOf("url1", "url2")
        val uploadNewImages = true
        val enabled = true

        updatePostUseCase(id, cities, minPrice, description, notAvailableDates, images, previousImages, uploadNewImages, enabled).collect { result ->
            when (result) {
                is Resource.Loading -> { this.cancel() }
                is Resource.Error -> { Assert.fail("Loading must be first") }
                is Resource.Success -> { Assert.fail("Loading must be first") }
            }
        }
    }
}