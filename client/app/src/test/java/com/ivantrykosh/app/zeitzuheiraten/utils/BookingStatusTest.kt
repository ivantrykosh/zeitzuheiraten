package com.ivantrykosh.app.zeitzuheiraten.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class BookingStatusTest {

    @Test
    fun `test string to value`() {
        assertEquals(BookingStatus.NOT_CONFIRMED, BookingStatus.stringToValue("NOT_CONFIRMED"))
        assertEquals(BookingStatus.CONFIRMED, BookingStatus.stringToValue("CONFIRMED"))
        assertEquals(BookingStatus.CANCELED, BookingStatus.stringToValue("CANCELED"))
        assertEquals(BookingStatus.SERVICE_PROVIDED, BookingStatus.stringToValue("SERVICE_PROVIDED"))
    }
}