package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.tasks.await

class FirestoreBookings(private val firestore: FirebaseFirestore = Firebase.firestore) {

    private lateinit var lastVisibleBooking: DocumentSnapshot

    suspend fun createBooking(userId: String, username: String, postId: String, category: String, providerId: String, provider: String, dateRange: DatePair) {
        val bookingData = mapOf(
            Booking::userId.name to userId,
            Booking::username.name to username,
            Booking::postId.name to postId,
            Booking::category.name to category,
            Booking::providerId.name to providerId,
            Booking::provider.name to provider,
            Booking::dateRange.name to dateRange,
            Booking::confirmed.name to false,
            Booking::canceled.name to false,
            Booking::serviceProvided.name to false,
        )
        firestore.collection(Collections.BOOKINGS)
            .document()
            .set(bookingData)
            .await()
    }

    suspend fun updateBooking(bookingId: String, dateRange: DatePair?, confirmed: Boolean?, canceled: Boolean?, serviceProvided: Boolean?) {
        val updatedData = mutableMapOf<String, Any>()
        if (dateRange != null) {
            updatedData[Booking::dateRange.name] = dateRange
        }
        if (confirmed != null) {
            updatedData[Booking::confirmed.name] = confirmed
        }
        if (canceled != null) {
            updatedData[Booking::canceled.name] = canceled
        }
        if (serviceProvided != null) {
            updatedData[Booking::serviceProvided.name] = serviceProvided
        }
        firestore.collection(Collections.BOOKINGS)
            .document(bookingId)
            .update(updatedData)
            .await()
    }

    suspend fun getBookingsForUser(userId: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType): List<Booking> {
        return firestore.collection(Collections.BOOKINGS)
            .whereEqualTo(Booking::userId.name, userId)
            .let {
                when (bookingsFilterType) {
                    BookingsFilterType.CANCELED -> it.whereEqualTo(Booking::canceled.name, true)
                    BookingsFilterType.SERVICE_PROVIDED -> it.whereEqualTo(Booking::serviceProvided.name, true)
                    BookingsFilterType.NOT_CONFIRMED -> it.whereEqualTo(Booking::canceled.name, false).whereEqualTo(Booking::confirmed.name, false)
                    BookingsFilterType.CONFIRMED -> it.whereEqualTo(Booking::canceled.name, false).whereEqualTo(Booking::serviceProvided.name, false).whereEqualTo(Booking::confirmed.name, true)
                }
            }
            .orderBy("${Booking::dateRange.name}.${DatePair::startDate.name}")
            .let {
                if (startAfterLast) {
                    it.startAfter(lastVisibleBooking)
                } else {
                    it
                }
            }
            .limit(pageSize.toLong())
            .get()
            .await()
            .also {
                if (!it.isEmpty) {
                    lastVisibleBooking = it.documents[it.size() - 1]
                }
            }
            .map { doc ->
                doc.toObject(Booking::class.java).copy(id = doc.id)
            }
    }

    suspend fun getBookingsForPost(postId: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType): List<Booking> {
        return firestore.collection(Collections.BOOKINGS)
            .whereEqualTo(Booking::postId.name, postId)
            .let {
                when (bookingsFilterType) {
                    BookingsFilterType.CANCELED -> it.whereEqualTo(Booking::canceled.name, true)
                    BookingsFilterType.SERVICE_PROVIDED -> it.whereEqualTo(Booking::serviceProvided.name, true)
                    BookingsFilterType.NOT_CONFIRMED -> it.whereEqualTo(Booking::canceled.name, false).whereEqualTo(Booking::confirmed.name, false)
                    BookingsFilterType.CONFIRMED -> it.whereEqualTo(Booking::canceled.name, false).whereEqualTo(Booking::serviceProvided.name, false).whereEqualTo(Booking::confirmed.name, true)
                }
            }
            .orderBy("${Booking::dateRange.name}.${DatePair::startDate.name}")
            .let {
                if (startAfterLast) {
                    it.startAfter(lastVisibleBooking)
                } else {
                    it
                }
            }
            .limit(pageSize.toLong())
            .get()
            .await()
            .also {
                if (!it.isEmpty) {
                    lastVisibleBooking = it.documents[it.size() - 1]
                }
            }
            .map { doc ->
                doc.toObject(Booking::class.java).copy(id = doc.id)
            }
    }

    suspend fun getBookingDatesForPost(postId: String): List<DatePair> {
        return firestore.collection(Collections.BOOKINGS)
            .whereEqualTo(Booking::postId.name, postId)
            .get()
            .await()
            .filter {
                !it.get(Booking::canceled.name, Boolean::class.java)!! &&
                !it.get(Booking::serviceProvided.name, Boolean::class.java)!! &&
                it.get(Booking::dateRange.name, DatePair::class.java)!!.endDate > System.currentTimeMillis() - 84_000_000
            }
            .map {
                it.get(Booking::dateRange.name, DatePair::class.java)!!
            }
    }
}