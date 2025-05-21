package com.ivantrykosh.app.zeitzuheiraten.utils

import android.content.Context
import com.ivantrykosh.app.zeitzuheiraten.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class BookingsFilterTypeTest {

    private lateinit var context: Context
    private val filters = mapOf(
        R.string.not_confirmed to "Not confirmed",
        R.string.confirmed to "Confirmed",
        R.string.canceled to "Canceled",
        R.string.service_provided to "Service provided",
    )

    @Before
    fun setup() {
        context = mock<Context> {
            for ((resId, value) in filters) {
                on { it.getString(resId) } doReturn value
            }
        }
    }

    @Test
    fun `get string test`() {
        assertEquals("Not confirmed", BookingsFilterType.NOT_CONFIRMED.getString(context))
        assertEquals("Confirmed", BookingsFilterType.CONFIRMED.getString(context))
        assertEquals("Canceled", BookingsFilterType.CANCELED.getString(context))
        assertEquals("Service provided", BookingsFilterType.SERVICE_PROVIDED.getString(context))
    }

    @Test
    fun `get object by string test`() {
        assertEquals(BookingsFilterType.NOT_CONFIRMED, BookingsFilterType.getObjectByString(context, "Not confirmed"))
        assertEquals(BookingsFilterType.CONFIRMED, BookingsFilterType.getObjectByString(context, "Confirmed"))
        assertEquals(BookingsFilterType.CANCELED, BookingsFilterType.getObjectByString(context, "Canceled"))
        assertEquals(BookingsFilterType.SERVICE_PROVIDED, BookingsFilterType.getObjectByString(context, "Service provided"))
    }

    @Test
    fun `objects to string list test`() {
        val strings = BookingsFilterType.toStringList(context)

        assertEquals("Not confirmed", strings[0])
        assertEquals("Confirmed", strings[1])
        assertEquals("Canceled", strings[2])
        assertEquals("Service provided", strings[3])
    }
}