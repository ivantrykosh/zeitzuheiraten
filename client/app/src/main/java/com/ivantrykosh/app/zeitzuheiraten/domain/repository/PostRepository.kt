package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.utils.OrderType

interface PostRepository {

    suspend fun createPost(post: PostWithRating)

    suspend fun getPostsByUserId(userId: String): List<PostWithRating>

    suspend fun getPostById(id: String): PostWithRating

    suspend fun getPostByFilters(category: String, city: String, minPrice: Int?, maxPrice: Int?, startAfterLast: Boolean, pageSize: Int, orderType: OrderType): List<PostWithRating>

    suspend fun updatePost(post: PostWithRating)

    suspend fun deletePost(postId: String)
}