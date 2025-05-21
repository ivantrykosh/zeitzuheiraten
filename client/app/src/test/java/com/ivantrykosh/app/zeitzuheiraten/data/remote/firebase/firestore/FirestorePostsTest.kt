package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Rating
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FirestorePostsTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var firestorePosts: FirestorePosts

    @Before
    fun setup() {
        mockCollectionReference = mock()
        mockFirestore = mock {
            on { it.collection(Collections.POSTS) } doReturn mockCollectionReference
        }
        firestorePosts = FirestorePosts(mockFirestore)
    }

    @Test
    fun `create post successfully`() = runTest {
        val post = PostWithRating(
            id = "postId",
            providerId = "providerId",
            providerName = "Provider Name",
            category = "Video",
            cities = listOf("Dnipro"),
            description = "Description",
            minPrice = 10000,
            photosUrl = listOf("url"),
            notAvailableDates = emptyList(),
            enabled = true,
            rating = Rating(),
            creationTime = 10000000L
        )
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(post.id) } doReturn it
            on { it.set(any()) } doReturn completedTask
        }

        firestorePosts.createPost(post)

        verify(mockFirestore).collection(Collections.POSTS)
        verify(mockCollectionReference).document(post.id)
        verify(documentRef).set(any())
    }

    @Test
    fun `get posts by user id successfully`() = runTest {
        val postId = "postId"
        val userId = "userId"
        val post = PostWithRating()
        val queryDocSnapshot = mock<QueryDocumentSnapshot> {
            on { it.id } doReturn postId
            onGeneric { it.toObject(PostWithRating::class.java) } doReturn post
        }
        val querySnapshot = mock<QuerySnapshot> {
            on { it.iterator() } doReturn mutableListOf(queryDocSnapshot).iterator()
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val query = mock<Query> {
            on { mockCollectionReference.whereEqualTo(PostWithRating::providerId.name, userId) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val posts = firestorePosts.getPostsByUserId(userId)

        verify(mockFirestore).collection(Collections.POSTS)
        verify(mockCollectionReference).whereEqualTo(PostWithRating::providerId.name, userId)
        verify(query).get()
        verify(queryDocSnapshot).toObject(PostWithRating::class.java)
        assertEquals(1, posts.size)
        assertEquals(postId, posts[0].id)
    }

    @Test
    fun `get post by id successfully`() = runTest {
        val postId = "postId"
        val testPost = PostWithRating(id = postId)
        val documentSnapshot = mock<DocumentSnapshot> {
            onGeneric { it.toObject(PostWithRating::class.java) } doReturn testPost
        }
        val completedTask = Tasks.forResult(documentSnapshot)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(postId) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val post = firestorePosts.getPostById(postId)

        verify(mockFirestore).collection(Collections.POSTS)
        verify(mockCollectionReference).document(postId)
        verify(documentRef).get()
        assertEquals(postId, post.id)
    }

    @Test
    fun `get posts by filters successfully`() = runTest {
        val postId = "postId"
        val category = "Video"
        val startAfterLast = false
        val pageSize = 10
        val postsOrderType = PostsOrderType.BY_PRICE_ASC
        val post = PostWithRating(id = postId)
        val docSnapshot = mock<DocumentSnapshot>()
        val queryDocSnapshot = mock<QueryDocumentSnapshot> {
            on { it.id } doReturn postId
            onGeneric { it.toObject(PostWithRating::class.java) } doReturn post
        }
        val querySnapshot = mock<QuerySnapshot> {
            onGeneric { it.isEmpty } doReturn false
            onGeneric { it.documents } doReturn listOf(docSnapshot)
            on { it.size() } doReturn 1
            on { it.iterator() } doReturn mutableListOf(queryDocSnapshot).iterator()
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val query = mock<Query> {
            on { mockCollectionReference.whereEqualTo(PostWithRating::enabled.name, true) } doReturn it
            on { it.whereEqualTo(PostWithRating::category.name, category) } doReturn it
            on { it.orderBy(PostWithRating::minPrice.name, Query.Direction.ASCENDING) } doReturn it
            on { it.limit(pageSize.toLong()) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val posts = firestorePosts.getPostsByFilters(category = category, city = "", minPrice = null, maxPrice = null, startAfterLast = startAfterLast, pageSize = pageSize, postsOrderType = postsOrderType)

        verify(mockFirestore).collection(Collections.POSTS)
        verify(mockCollectionReference).whereEqualTo(PostWithRating::enabled.name, true)
        verify(query).whereEqualTo(PostWithRating::category.name, category)
        verify(query).orderBy(PostWithRating::minPrice.name, Query.Direction.ASCENDING)
        verify(query).limit(pageSize.toLong())
        verify(query).get()
        verify(queryDocSnapshot).toObject(PostWithRating::class.java)
        assertEquals(1, posts.size)
        assertEquals(postId, posts[0].id)
    }

    @Test
    fun `update post successfully`() = runTest {
        val post = PostWithRating(
            id = "postId",
            providerId = "providerId",
            providerName = "Provider Name",
            category = "Video",
            cities = listOf("Dnipro"),
            description = "Description",
            minPrice = 10000,
            photosUrl = listOf("url"),
            notAvailableDates = emptyList(),
            enabled = true,
            rating = Rating(),
            creationTime = 10000000L
        )
        val postData = mapOf(
            PostWithRating::cities.name to post.cities,
            PostWithRating::description.name to post.description,
            PostWithRating::minPrice.name to post.minPrice,
            PostWithRating::photosUrl.name to post.photosUrl,
            PostWithRating::notAvailableDates.name to post.notAvailableDates,
            PostWithRating::enabled.name to post.enabled,
        )
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(post.id) } doReturn it
            on { it.update(postData) } doReturn completedTask
        }

        firestorePosts.updatePost(post)

        verify(mockFirestore).collection(Collections.POSTS)
        verify(mockCollectionReference).document(post.id)
        verify(documentRef).update(postData)
    }

    @Test
    fun `delete post successfully`() = runTest {
        val postId = "postId"
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(postId) } doReturn it
            on { it.delete() } doReturn completedTask
        }

        firestorePosts.deletePost(postId)

        verify(mockFirestore).collection(Collections.POSTS)
        verify(mockCollectionReference).document(postId)
        verify(documentRef).delete()
    }
}