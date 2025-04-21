package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestorePosts
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.OrderType
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestorePosts: FirestorePosts,
) : PostRepository {
    override suspend fun createPost(post: PostWithRating) {
        firestorePosts.createPost(post)
    }

    override suspend fun getPostsByUserId(userId: String): List<PostWithRating> {
        return firestorePosts.getPostsByUserId(userId)
    }

    override suspend fun getPostById(id: String): PostWithRating {
        return firestorePosts.getPostById(id)
    }

    override suspend fun getPostByFilters(category: String, city: String, minPrice: Int?, maxPrice: Int?, startAfterLast: Boolean, pageSize: Int, orderType: OrderType): List<PostWithRating> {
        return firestorePosts.getPostsByFilters(category, city, minPrice, maxPrice, startAfterLast, pageSize, orderType)
    }

    override suspend fun updatePost(post: PostWithRating) {
        firestorePosts.updatePost(post)
    }

    override suspend fun deletePost(postId: String) {
        firestorePosts.deletePost(postId)
    }
}