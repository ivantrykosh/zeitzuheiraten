package com.ivantrykosh.app.zeitzuheiraten.data.repository

import android.net.Uri
import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.storage.FirebaseStorage
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class FirebaseStorageRepositoryImplTest {

    private lateinit var mockFirebaseStorage: FirebaseStorage
    private lateinit var firebaseStorageRepositoryImpl: FirebaseStorageRepositoryImpl

    @Before
    fun setup() {
        mockFirebaseStorage = mock()
        firebaseStorageRepositoryImpl = FirebaseStorageRepositoryImpl(mockFirebaseStorage)
    }

    @Test
    fun `upload image successfully`() = runTest {
        val name = "userid/test.jpg"
        val imageUri = mock<Uri>()
        val downloadUrl = "some-bucket/$name"
        whenever(mockFirebaseStorage.uploadImage(name, imageUri)).doReturn(downloadUrl)

        val url = firebaseStorageRepositoryImpl.uploadImage(name, imageUri)

        verify(mockFirebaseStorage).uploadImage(name, imageUri)
        Assert.assertEquals(downloadUrl, url)
    }

    @Test
    fun `delete image successfully`() = runTest {
        val name = "userid/test.jpg"
        whenever(mockFirebaseStorage.deleteImageOrFolder(name)).doReturn(Unit)

        firebaseStorageRepositoryImpl.deleteImageOrFolder(name)

        verify(mockFirebaseStorage).deleteImageOrFolder(name)
    }

    @Test
    fun `delete folder successfully`() = runTest {
        val name = "userid"
        whenever(mockFirebaseStorage.deleteImageOrFolder(name)).doReturn(Unit)

        firebaseStorageRepositoryImpl.deleteImageOrFolder(name)

        verify(mockFirebaseStorage).deleteImageOrFolder(name)
    }
}