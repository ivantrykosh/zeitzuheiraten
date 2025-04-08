package com.ivantrykosh.app.zeitzuheiraten.data.repository

import com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore.FirestorePosts
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestorePosts: FirestorePosts,
) : PostRepository {
    override suspend fun createPost(post: Post) {
        firestorePosts.createPost(post)
    }

    override suspend fun getPostsByUserId(userId: String): List<Post> {
        return firestorePosts.getPostsByUserId(userId)
    }

    override suspend fun getPostById(id: String): Post {
        return firestorePosts.getPostById(id)
    }

    override suspend fun getPostByCategory(category: String, pageIndex: Int, pageSize: Int): List<Post> {
        return firestorePosts.getPostsByCategory(category, pageIndex, pageSize)
    }

    override suspend fun updatePost(post: Post) {
        firestorePosts.updatePost(post)
    }

    override suspend fun deletePost(postId: String) {
        firestorePosts.deletePost(postId)
    }
}