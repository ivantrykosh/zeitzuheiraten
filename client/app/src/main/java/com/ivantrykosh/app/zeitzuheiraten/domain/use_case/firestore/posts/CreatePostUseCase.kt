package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.net.Uri
import android.util.Log
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Rating
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FirebaseStorageRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

private const val LOG_TAG = "CreatePostUseCase"

class CreatePostUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
) {
    operator fun invoke(category: String, cities: List<String>, minPrice: Int, description: String, notAvailableDates: List<DatePair>, images: List<Uri>) = flow<Resource<Unit>> {
        val downloadUrls = mutableListOf<String>()
        val postId = UUID.randomUUID().toString()
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            val user = userRepository.getUserById(userId)
            images.forEachIndexed { index, image ->
                if (image != Uri.EMPTY) {
                    val name = "$userId/$postId/$index"
                    downloadUrls.add(firebaseStorageRepository.uploadImage(name, image))
                }
            }
            val dateTime = Instant.now().toEpochMilli()
            val post = PostWithRating(
                id = postId,
                providerId = userId,
                providerName = user.name,
                category = category,
                cities = cities,
                description = description,
                minPrice = minPrice,
                photosUrl = downloadUrls,
                notAvailableDates = notAvailableDates,
                enabled = true,
                rating = Rating(0.0, 0),
                creationTime = dateTime
            )
            postRepository.createPost(post)
            emit(Resource.Success())
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "An error occurred")
            try {
                val userId = userAuthRepository.getCurrentUserId()
                if (userId.isNotEmpty()) {
                    if (downloadUrls.isNotEmpty()) {
                        downloadUrls.forEachIndexed { index, _ ->
                            firebaseStorageRepository.deleteImage("$userId/$postId/$index")
                        }
                    }
                }
            } catch (exception: Exception) {
                Log.e(LOG_TAG, exception.message ?: "An error occurred")
            }
            emit(Resource.Error(e))
        }
    }
}