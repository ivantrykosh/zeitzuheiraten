package com.ivantrykosh.app.zeitzuheiraten.domain.repository

import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post

interface PostRepository {

    suspend fun createPost(post: Post)

    suspend fun getPostsByUserId(userId: String): List<Post>

    suspend fun getPostById(id: String): Post

    suspend fun getPostByCategory(category: String, pageIndex: Int, pageSize: Int): List<Post>

    suspend fun updatePost(post: Post)

    suspend fun deletePost(postId: String)
}