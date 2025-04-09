package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post

interface PostRepository {

    suspend fun createPost(post: Post)

    suspend fun getPostsByUserId(userId: String): List<Post>

    suspend fun getPostById(id: String): Post

    suspend fun getPostByFilters(category: String, city: String, maxPrice: Int?, startAfterLast: Boolean, pageSize: Int): List<Post>

    suspend fun updatePost(post: Post)

    suspend fun deletePost(postId: String)
}