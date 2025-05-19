package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Rating
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
import kotlinx.coroutines.tasks.await

class FirestorePosts(private val firestore: FirebaseFirestore = Firebase.firestore) {

    private lateinit var lastVisiblePost: DocumentSnapshot

    suspend fun createPost(post: PostWithRating) {
        val postData = mapOf(
            PostWithRating::providerId.name to post.providerId,
            PostWithRating::providerName.name to post.providerName,
            PostWithRating::category.name to post.category,
            PostWithRating::cities.name to post.cities,
            PostWithRating::description.name to post.description,
            PostWithRating::minPrice.name to post.minPrice,
            PostWithRating::photosUrl.name to post.photosUrl,
            PostWithRating::notAvailableDates.name to post.notAvailableDates,
            PostWithRating::enabled.name to post.enabled,
            PostWithRating::rating.name to post.rating,
            PostWithRating::creationTime.name to post.creationTime,
        )
        firestore.collection(Collections.POSTS)
            .document(post.id)
            .set(postData)
            .await()
    }

    suspend fun getPostsByUserId(userId: String): List<PostWithRating> {
        return firestore.collection(Collections.POSTS)
            .whereEqualTo(PostWithRating::providerId.name, userId)
            .get()
            .await()
            .map { doc ->
                doc.toObject(PostWithRating::class.java).copy(id = doc.id)
            }
    }

    suspend fun getPostById(id: String): PostWithRating {
        return firestore.collection(Collections.POSTS)
            .document(id)
            .get()
            .await()
            .toObject(PostWithRating::class.java)!!
            .copy(id = id)
    }

    suspend fun getPostsByFilters(category: String, city: String, minPrice: Int?, maxPrice: Int?, startAfterLast: Boolean, pageSize: Int, postsOrderType: PostsOrderType): List<PostWithRating> {
        return firestore.collection(Collections.POSTS)
            .let {
                var query: Query = it.whereEqualTo(PostWithRating::enabled.name, true)
                if (category.isNotEmpty()) {
                    query = query.whereEqualTo(PostWithRating::category.name, category)
                }
                if (city.isNotEmpty()) {
                    query = query.whereArrayContains(PostWithRating::cities.name, city)
                }
                if (minPrice != null && minPrice >= 0) {
                    query = query.whereGreaterThanOrEqualTo(PostWithRating::minPrice.name, minPrice)
                }
                if (maxPrice != null && maxPrice > 0) {
                    query = query.whereLessThanOrEqualTo(PostWithRating::minPrice.name, maxPrice)
                }
                query
            }
            .let {
                when (postsOrderType) {
                    PostsOrderType.BY_CATEGORY -> it.orderBy(PostWithRating::category.name)
                    PostsOrderType.BY_RATING_DESC -> it.orderBy("${PostWithRating::rating.name}.${Rating::rating.name}", Query.Direction.DESCENDING)
                    PostsOrderType.BY_NUMBER_OF_FEEDBACKS -> it.orderBy("${PostWithRating::rating.name}.${Rating::numberOfFeedbacks.name}", Query.Direction.DESCENDING)
                    PostsOrderType.BY_PRICE_ASC -> it.orderBy(PostWithRating::minPrice.name, Query.Direction.ASCENDING)
                    PostsOrderType.BY_PRICE_DESC -> it.orderBy(PostWithRating::minPrice.name, Query.Direction.DESCENDING)
                }
            }
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
                doc.toObject(PostWithRating::class.java).copy(id = doc.id)
            }
    }

    suspend fun updatePost(post: PostWithRating) {
        val postData = mapOf(
            PostWithRating::cities.name to post.cities,
            PostWithRating::description.name to post.description,
            PostWithRating::minPrice.name to post.minPrice,
            PostWithRating::photosUrl.name to post.photosUrl,
            PostWithRating::notAvailableDates.name to post.notAvailableDates,
            PostWithRating::enabled.name to post.enabled,
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