package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.tasks.await

class FirestorePosts(private val firestore: FirebaseFirestore = Firebase.firestore) {

    private lateinit var lastVisiblePost: DocumentSnapshot

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
            .document(post.id)
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

    suspend fun getPostById(id: String): Post {
        return firestore.collection(Collections.POSTS)
            .document(id)
            .get()
            .await()
            .toObject(Post::class.java)!!
            .copy(id = id)
    }

    suspend fun getPostsByFilters(category: String, city: String, minPrice: Int?, maxPrice: Int?, startAfterLast: Boolean, pageSize: Int): List<Post> {
        return firestore.collection(Collections.POSTS)
            .let {
                var query: Query? = null
                if (category.isNotEmpty()) {
                    query = it.whereEqualTo(Post::category.name, category)
                }
                if (city.isNotEmpty()) {
                    query = (query ?: it).whereArrayContains(Post::cities.name, city)
                }
                if (minPrice != null && minPrice >= 0) {
                    query = (query ?: it).whereGreaterThanOrEqualTo(Post::minPrice.name, minPrice)
                }
                if (maxPrice != null && maxPrice > 0) {
                    query = (query ?: it).whereLessThanOrEqualTo(Post::minPrice.name, maxPrice)
                }
                query ?: it
            }
            .orderBy(Post::category.name) // todo change order
            .let {
                if (startAfterLast) {
                    it.startAfter(lastVisiblePost)
                } else {
                    it
                }
            }
            .limit(pageSize.toLong())
            .get()
            .await()
            .also {
                 if (!it.isEmpty) {
                     lastVisiblePost = it.documents[it.size() - 1]
                }
            }
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