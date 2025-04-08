package com.ivantrykosh.app.zeitzuheiraten.domain.use_case.firestore.posts

import android.net.Uri
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.FirebaseStorageRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.PostRepository
import com.ivantrykosh.app.zeitzuheiraten.domain.repository.UserAuthRepository
import com.ivantrykosh.app.zeitzuheiraten.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdatePostUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val postRepository: PostRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
) {
    operator fun invoke(id: String, cities: List<String>, minPrice: Int, description: String, notAvailableDates: List<DatePair>, images: List<Uri>, previousImages: List<String>, uploadNewImages: Boolean) = flow<Resource<Post>> {
        val downloadUrls = mutableListOf<String>()
        try {
            emit(Resource.Loading())
            val userId = userAuthRepository.getCurrentUserId()
            if (uploadNewImages) {
                for (index in images.size..previousImages.lastIndex) {
                    firebaseStorageRepository.deleteImage("$userId/$id/$index")
                }
                images.forEachIndexed { index, image ->
                    downloadUrls.add(firebaseStorageRepository.uploadImage("$userId/$id/$index", image))
                }
            }
            val post = Post(
                id = id,
                cities = cities,
                minPrice = minPrice,
                description = description,
                notAvailableDates = notAvailableDates,
                photosUrl = if (uploadNewImages) downloadUrls else previousImages
            )
            postRepository.updatePost(post)
            emit(Resource.Success(post))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}