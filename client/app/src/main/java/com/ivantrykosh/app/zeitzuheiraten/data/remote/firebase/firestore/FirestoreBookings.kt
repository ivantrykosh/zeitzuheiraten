package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingStatus
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.tasks.await
import java.time.Instant

class FirestoreBookings(
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val functions: FirebaseFunctions = Firebase.functions,
) {

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
            Booking::status.name to BookingStatus.NOT_CONFIRMED.name,
            Booking::creationTime.name to Instant.now().toEpochMilli()
        )
        firestore.collection(Collections.BOOKINGS)
            .document()
            .set(bookingData)
            .await()
    }

    suspend fun createBookingWithLock(userId: String, username: String, postId: String, category: String, providerId: String, provider: String, dateRange: DatePair) {
        val bookingData = mapOf(
            Booking::userId.name to userId,
            Booking::username.name to username,
            Booking::postId.name to postId,
            Booking::category.name to category,
            Booking::providerId.name to providerId,
            Booking::provider.name to provider,
            Booking::dateRange.name to mapOf(
                DatePair::startDate.name to dateRange.startDate,
                DatePair::endDate.name to dateRange.endDate,
            ),
            Booking::status.name to BookingStatus.NOT_CONFIRMED.name,
            Booking::creationTime.name to Instant.now().toEpochMilli()
        )
        functions
            .getHttpsCallable("createBookingWithLock")
            .call(bookingData)
            .await()
    }

    suspend fun updateBookingDateRange(bookingId: String, dateRange: DatePair) {
        val updatedData = mapOf(
            Booking::dateRange.name to dateRange
        )
        firestore.collection(Collections.BOOKINGS)
            .document(bookingId)
            .update(updatedData)
            .await()
    }

    suspend fun updateBookingDateRangeWithLock(bookingId: String, dateRange: DatePair) {
        val updatedData = mapOf(
            Booking::id.name to bookingId,
            Booking::dateRange.name to mapOf(
                DatePair::startDate.name to dateRange.startDate,
                DatePair::endDate.name to dateRange.endDate,
            ),
        )
        functions
            .getHttpsCallable("updateBookingDateRange")
            .call(updatedData)
            .await()
    }

    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        val updatedData = mapOf(
            Booking::id.name to bookingId,
            Booking::status.name to status.name
        )
        functions
            .getHttpsCallable("updateBookingStatus")
            .call(updatedData)
            .await()
    }

    suspend fun getBookingsForUser(userId: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType): List<Booking> {
        return firestore.collection(Collections.BOOKINGS)
            .whereEqualTo(Booking::userId.name, userId)
            .let {
                when (bookingsFilterType) {
                    BookingsFilterType.CANCELED -> it.whereEqualTo(Booking::status.name, BookingStatus.CANCELED.name)
                    BookingsFilterType.SERVICE_PROVIDED -> it.whereEqualTo(Booking::status.name, BookingStatus.SERVICE_PROVIDED.name)
                    BookingsFilterType.NOT_CONFIRMED -> it.whereEqualTo(Booking::status.name, BookingStatus.NOT_CONFIRMED.name)
                    BookingsFilterType.CONFIRMED -> it.whereEqualTo(Booking::status.name, BookingStatus.CONFIRMED.name)
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
                    BookingsFilterType.CANCELED -> it.whereEqualTo(Booking::status.name, BookingStatus.CANCELED.name)
                    BookingsFilterType.SERVICE_PROVIDED -> it.whereEqualTo(Booking::status.name, BookingStatus.SERVICE_PROVIDED.name)
                    BookingsFilterType.NOT_CONFIRMED -> it.whereEqualTo(Booking::status.name, BookingStatus.NOT_CONFIRMED.name)
                    BookingsFilterType.CONFIRMED -> it.whereEqualTo(Booking::status.name, BookingStatus.CONFIRMED.name)
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
            .whereIn(Booking::status.name, listOf(BookingStatus.NOT_CONFIRMED, BookingStatus.CONFIRMED))
            .whereGreaterThan("${Booking::dateRange.name}.${DatePair::endDate.name}", Instant.now().toEpochMilli() - 84_000_000)
            .get()
            .await()
            .map {
                it.get(Booking::dateRange.name, DatePair::class.java)!!
            }
    }
}