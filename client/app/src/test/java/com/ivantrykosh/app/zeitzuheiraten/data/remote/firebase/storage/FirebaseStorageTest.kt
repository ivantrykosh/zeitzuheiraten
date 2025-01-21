package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.storage

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FirebaseStorageTest {

    private lateinit var mockFirebaseStorage: FirebaseStorage
    private lateinit var firebaseStorage: com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.storage.FirebaseStorage

    @Before
    fun setup() {
        mockFirebaseStorage = mock()
        firebaseStorage = FirebaseStorage(mockFirebaseStorage)
    }

    @Test
    fun `upload image successfully`() = runTest {
        val name = "userid/test.jpg"
        val imageUri = mock<Uri>()
        val downloadUrl = "some-bucket/$name"
        val childRef = mock<StorageReference>()
        val uploadTask = mock<UploadTask> {
            on { childRef.putFile(imageUri) } doReturn it
            on { it.isComplete } doReturn true
            on { it.exception } doReturn null
        }
        val taskSnapshot = mock<UploadTask.TaskSnapshot> {
            on { uploadTask.result } doReturn it
        }
        val metadata = mock<StorageMetadata>()
        val reference = mock<StorageReference>()
        mock<Uri> {
            onGeneric { taskSnapshot.metadata } doReturn metadata
            onGeneric { metadata.reference } doReturn reference
            on { reference.downloadUrl } doReturn Tasks.forResult(it)
            on { it.toString() } doReturn downloadUrl
        }
        mock<StorageReference> {
            on { mockFirebaseStorage.reference } doReturn it
            on { it.child(name) } doReturn childRef
        }

        val url = firebaseStorage.uploadImage(name, imageUri)

        Assert.assertEquals(downloadUrl, url)
    }

    @Test
    fun `delete image successfully`() = runTest {
        val name = "userid/test.jpg"
        val childRef = mock<StorageReference>()
        mock<StorageReference> {
            on { mockFirebaseStorage.reference } doReturn it
            on { it.child(name) } doReturn childRef
        }
        mock<Task<Void>> {
            on { childRef.delete() } doReturn it
            on { it.isComplete } doReturn true
            on { it.exception } doReturn null
        }

        firebaseStorage.deleteImageOrFolder(name)

        verify(mockFirebaseStorage).reference
        verify(mockFirebaseStorage.reference).child(name)
        verify(mockFirebaseStorage.reference.child(name)).delete()
    }

    @Test
    fun `delete user folder successfully`() = runTest {
        val name = "userid"
        val childRef = mock<StorageReference>()
        mock<StorageReference> {
            on { mockFirebaseStorage.reference } doReturn it
            on { it.child(name) } doReturn childRef
        }
        mock<Task<Void>> {
            on { childRef.delete() } doReturn it
            on { it.isComplete } doReturn true
            on { it.exception } doReturn null
        }

        firebaseStorage.deleteImageOrFolder(name)

        verify(mockFirebaseStorage).reference
        verify(mockFirebaseStorage.reference).child(name)
        verify(mockFirebaseStorage.reference.child(name)).delete()
    }
}