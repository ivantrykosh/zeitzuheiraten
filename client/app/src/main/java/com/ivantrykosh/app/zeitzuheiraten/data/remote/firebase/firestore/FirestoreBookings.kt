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

    /**
     * Create booking with provided data, [BookingStatus.NOT_CONFIRMED]  status and current creation UTC time.
     * This method used for booking uncommon categories, where booking is not unique for date
     */
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
            Booking::creatingDateTime.name to Instant.now().toEpochMilli()
        )
        firestore.collection(Collections.BOOKINGS)
            .document()
            .set(bookingData)
            .await()
    }

    /**
     * Create booking with provided data, [BookingStatus.NOT_CONFIRMED]  status and current creation UTC time.
     * This method used for booking common categories, where booking is unique for date
     */
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
            Booking::creatingDateTime.name to Instant.now().toEpochMilli()
        )
        functions
            .getHttpsCallable("createBookingWithLock")
            .call(bookingData)
            .await()
    }

    /**
     * Change the booking date for uncommon categories
     */
    suspend fun updateBookingDateRange(bookingId: String, dateRange: DatePair) {
        val updatedData = mapOf(
            Booking::dateRange.name to dateRange
        )
        firestore.collection(Collections.BOOKINGS)
            .document(bookingId)
            .update(updatedData)
            .await()
    }

    /**
     * Change the booking date for common categories
     */
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
        return getBookingByField(Booking::userId.name, userId, startAfterLast, pageSize, bookingsFilterType)
    }

    suspend fun getBookingsForPost(postId: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType): List<Booking> {
        return getBookingByField(Booking::postId.name, postId, startAfterLast, pageSize, bookingsFilterType)
    }

    /**
     * Get booking by field name and value, filter them by status, order by start date and paginate
     */
    private suspend fun getBookingByField(field: String, fieldValue: String, startAfterLast: Boolean, pageSize: Int, bookingsFilterType: BookingsFilterType): List<Booking> {
        return firestore.collection(Collections.BOOKINGS)
            .whereEqualTo(field, fieldValue)
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

    /**
     * Get booked dates for post.
     * Only includes bookings with status [BookingStatus.NOT_CONFIRMED] or [BookingStatus.CONFIRMED]
     * and with end date that is today or later.
     */
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