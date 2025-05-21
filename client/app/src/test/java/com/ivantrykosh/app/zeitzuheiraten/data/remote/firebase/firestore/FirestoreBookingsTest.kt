package com.ivantrykosh.app.zeitzuheiraten.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import com.google.firebase.functions.HttpsCallableResult
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingStatus
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType
import com.ivantrykosh.app.zeitzuheiraten.utils.Collections
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FirestoreBookingsTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockFunctions: FirebaseFunctions
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var firestoreBookings: FirestoreBookings

    @Before
    fun setup() {
        mockCollectionReference = mock()
        mockFirestore = mock {
            on { it.collection(Collections.BOOKINGS) } doReturn mockCollectionReference
        }
        mockFunctions = mock()
        firestoreBookings = FirestoreBookings(mockFirestore, mockFunctions)
    }

    @Test
    fun `create booking successfully`() = runTest {
        val userId = "userId"
        val username = "User Name"
        val postId = "postId"
        val category = "Video"
        val providerId = "providerId"
        val provider = "Provider Name"
        val dateRange = DatePair(0L, 86400000L)
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document() } doReturn it
            on { it.set(any()) } doReturn completedTask
        }

        firestoreBookings.createBooking(userId, username, postId, category, providerId, provider, dateRange)

        verify(mockFirestore).collection(Collections.BOOKINGS)
        verify(mockCollectionReference).document()
        verify(documentRef).set(any())
    }

    @Test
    fun `create booking with lock successfully`() = runTest {
        val userId = "userId"
        val username = "User Name"
        val postId = "postId"
        val category = "Video"
        val providerId = "providerId"
        val provider = "Provider Name"
        val dateRange = DatePair(0L, 86400000L)
        val completedTask = Tasks.forResult<HttpsCallableResult>(null)
        val httpCallableRef = mock<HttpsCallableReference> {
            on { mockFunctions.getHttpsCallable("createBookingWithLock") } doReturn it
            on { it.call(any()) } doReturn completedTask
        }

        firestoreBookings.createBookingWithLock(userId, username, postId, category, providerId, provider, dateRange)

        verify(mockFunctions).getHttpsCallable("createBookingWithLock")
        verify(httpCallableRef).call(any())
    }

    @Test
    fun `update booking date range successfully`() = runTest {
        val bookingId = "bookingId"
        val dateRange = DatePair(0L, 86400000L)
        val updatedData = mapOf(
            Booking::dateRange.name to dateRange
        )
        val completedTask = Tasks.forResult<Void>(null)
        val documentRef = mock<DocumentReference> {
            on { mockCollectionReference.document(bookingId) } doReturn it
            on { it.update(updatedData) } doReturn completedTask
        }

        firestoreBookings.updateBookingDateRange(bookingId, dateRange)

        verify(mockFirestore).collection(Collections.BOOKINGS)
        verify(mockCollectionReference).document(bookingId)
        verify(documentRef).update(updatedData)
    }

    @Test
    fun `update booking date range with lock successfully`() = runTest {
        val bookingId = "bookingId"
        val dateRange = DatePair(0L, 86400000L)
        val updatedData = mapOf(
            Booking::id.name to bookingId,
            Booking::dateRange.name to mapOf(
                DatePair::startDate.name to dateRange.startDate,
                DatePair::endDate.name to dateRange.endDate,
            ),
        )
        val completedTask = Tasks.forResult<HttpsCallableResult>(null)
        val httpCallableRef = mock<HttpsCallableReference> {
            on { mockFunctions.getHttpsCallable("updateBookingDateRange") } doReturn it
            on { it.call(updatedData) } doReturn completedTask
        }

        firestoreBookings.updateBookingDateRangeWithLock(bookingId, dateRange)

        verify(mockFunctions).getHttpsCallable("updateBookingDateRange")
        verify(httpCallableRef).call(updatedData)
    }

    @Test
    fun `update booking status with lock successfully`() = runTest {
        val bookingId = "bookingId"
        val status = BookingStatus.CONFIRMED
        val updatedData = mapOf(
            Booking::id.name to bookingId,
            Booking::status.name to status.name
        )
        val completedTask = Tasks.forResult<HttpsCallableResult>(null)
        val httpCallableRef = mock<HttpsCallableReference> {
            on { mockFunctions.getHttpsCallable("updateBookingStatus") } doReturn it
            on { it.call(updatedData) } doReturn completedTask
        }

        firestoreBookings.updateBookingStatus(bookingId, status)

        verify(mockFunctions).getHttpsCallable("updateBookingStatus")
        verify(httpCallableRef).call(updatedData)
    }

    @Test
    fun `get bookings for user successfully`() = runTest {
        val bookingId = "bookingId"
        val userId = "userId"
        val startAfterLast = false
        val pageSize = 10
        val bookingsFilterType = BookingsFilterType.CONFIRMED
        val booking = Booking()
        val docSnapshot = mock<DocumentSnapshot>()
        val queryDocSnapshot = mock<QueryDocumentSnapshot> {
            on { it.id } doReturn bookingId
            onGeneric { it.toObject(Booking::class.java) } doReturn booking
        }
        val querySnapshot = mock<QuerySnapshot> {
            onGeneric { it.isEmpty } doReturn false
            onGeneric { it.documents } doReturn listOf(docSnapshot)
            on { it.size() } doReturn 1
            on { it.iterator() } doReturn mutableListOf(queryDocSnapshot).iterator()
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val query = mock<Query> {
            on { mockCollectionReference.whereEqualTo(Booking::userId.name, userId) } doReturn it
            on { it.whereEqualTo(Booking::status.name, BookingStatus.CONFIRMED.name) } doReturn it
            on { it.orderBy("${Booking::dateRange.name}.${DatePair::startDate.name}") } doReturn it
            on { it.limit(pageSize.toLong()) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val bookings = firestoreBookings.getBookingsForUser(userId, startAfterLast, pageSize, bookingsFilterType)

        verify(mockFirestore).collection(Collections.BOOKINGS)
        verify(mockCollectionReference).whereEqualTo(Booking::userId.name, userId)
        verify(query).whereEqualTo(Booking::status.name, BookingStatus.CONFIRMED.name)
        verify(query).orderBy("${Booking::dateRange.name}.${DatePair::startDate.name}")
        verify(query).limit(pageSize.toLong())
        verify(query).get()
        verify(queryDocSnapshot).toObject(Booking::class.java)
        assertEquals(1, bookings.size)
        assertEquals(bookingId, bookings[0].id)
    }

    @Test
    fun `get bookings for post successfully`() = runTest {
        val bookingId = "bookingId"
        val postId = "postId"
        val startAfterLast = false
        val pageSize = 10
        val bookingsFilterType = BookingsFilterType.CONFIRMED
        val booking = Booking()
        val docSnapshot = mock<DocumentSnapshot>()
        val queryDocSnapshot = mock<QueryDocumentSnapshot> {
            on { it.id } doReturn bookingId
            onGeneric { it.toObject(Booking::class.java) } doReturn booking
        }
        val querySnapshot = mock<QuerySnapshot> {
            onGeneric { it.isEmpty } doReturn false
            onGeneric { it.documents } doReturn listOf(docSnapshot)
            on { it.size() } doReturn 1
            on { it.iterator() } doReturn mutableListOf(queryDocSnapshot).iterator()
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val query = mock<Query> {
            on { mockCollectionReference.whereEqualTo(Booking::postId.name, postId) } doReturn it
            on { it.whereEqualTo(Booking::status.name, BookingStatus.CONFIRMED.name) } doReturn it
            on { it.orderBy("${Booking::dateRange.name}.${DatePair::startDate.name}") } doReturn it
            on { it.limit(pageSize.toLong()) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val bookings = firestoreBookings.getBookingsForPost(postId, startAfterLast, pageSize, bookingsFilterType)

        verify(mockFirestore).collection(Collections.BOOKINGS)
        verify(mockCollectionReference).whereEqualTo(Booking::postId.name, postId)
        verify(query).whereEqualTo(Booking::status.name, BookingStatus.CONFIRMED.name)
        verify(query).orderBy("${Booking::dateRange.name}.${DatePair::startDate.name}")
        verify(query).limit(pageSize.toLong())
        verify(query).get()
        verify(queryDocSnapshot).toObject(Booking::class.java)
        assertEquals(1, bookings.size)
        assertEquals(bookingId, bookings[0].id)
    }

    @Test
    fun `get booking dates for post`() = runTest {
        val postId = "postId"
        val datePair = DatePair()
        val queryDocSnapshot = mock<QueryDocumentSnapshot> {
            onGeneric { it.get(Booking::dateRange.name, DatePair::class.java) } doReturn datePair
        }
        val querySnapshot = mock<QuerySnapshot> {
            on { it.iterator() } doReturn mutableListOf(queryDocSnapshot).iterator()
        }
        val completedTask = Tasks.forResult<QuerySnapshot>(querySnapshot)
        val query = mock<Query> {
            on { mockCollectionReference.whereEqualTo(Booking::postId.name, postId) } doReturn it
            on { it.whereIn(Booking::status.name, listOf(BookingStatus.NOT_CONFIRMED, BookingStatus.CONFIRMED)) } doReturn it
            on { it.whereGreaterThan(eq("${Booking::dateRange.name}.${DatePair::endDate.name}"), any()) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val dates = firestoreBookings.getBookingDatesForPost(postId)

        verify(mockFirestore).collection(Collections.BOOKINGS)
        verify(mockCollectionReference).whereEqualTo(Booking::postId.name, postId)
        verify(query).whereIn(Booking::status.name, listOf(BookingStatus.NOT_CONFIRMED, BookingStatus.CONFIRMED))
        verify(query).whereGreaterThan(eq("${Booking::dateRange.name}.${DatePair::endDate.name}"), any())
        verify(query).get()
        verify(queryDocSnapshot).get(Booking::dateRange.name, DatePair::class.java)
        assertEquals(1, dates.size)
        assertEquals(datePair, dates[0])
    }
}