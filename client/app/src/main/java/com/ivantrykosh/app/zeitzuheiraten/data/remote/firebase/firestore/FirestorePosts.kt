package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.tasks.await

class FirestorePosts(private val firestore: FirebaseFirestore = Firebase.firestore) {

    suspend fun createPost(post: Post) {
        val postData = mapOf(
            Post::providerId.name to post.providerId,
            Post::providerName.name to post.providerName,
            Post::category.name to post.category,
            Post::cities.name to post.cities,
            Post::description.name to post.description,
            Post::minPrice.name to post.minPrice,
            Post::photosUrl.name to post.photosUrl,
            Post::notAvailableDates.name to post.notAvailableDates,
        )
        firestore.collection(Collections.POSTS)
            .document()
            .set(postData)
            .await()
    }

    suspend fun getPostsByUserId(userId: String): List<Post> {
        return firestore.collection(Collections.POSTS)
            .whereEqualTo(Post::providerId.name, userId)
            .get()
            .await()
            .map { doc ->
                doc.toObject(Post::class.java).copy(id = doc.id)
            }
    }

    suspend fun updatePost(post: Post) {
        val postData = mapOf(
            Post::cities.name to post.cities,
            Post::description.name to post.description,
            Post::minPrice.name to post.minPrice,
            Post::photosUrl.name to post.photosUrl,
            Post::notAvailableDates.name to post.notAvailableDates,
        )
        firestore.collection(Collections.POSTS)
            .document(post.id)
            .update(postData)
            .await()
    }

    suspend fun deletePost(postId: String) {
        firestore.collection(Collections.POSTS)
            .document(postId)
            .delete()
            .await()
    }
}