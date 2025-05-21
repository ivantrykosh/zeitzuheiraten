package com.ivantrykosh.app.zeitzuheiraten.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.TimeZone

class UtilsTest {

    @Test
    fun `test date to string date in different time zones`() {
        val date1 = 1747833909779L
        val date2 = 1740833909779L
        val date3 = 1750833909779L

        TimeZone.setDefault(TimeZone.getTimeZone("UK/London"))
        val date1String = date1.toStringDate()
        TimeZone.setDefault(TimeZone.getTimeZone("Ukraine/Kyiv"))
        val date2String = date2.toStringDate()
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
        val date3String = date3.toStringDate()

        assertEquals("2025/05/21", date1String)
        assertEquals("2025/03/01", date2String)
        assertEquals("2025/06/24", date3String)
    }

    @Test
    fun `test date to string date time in different time zones`() {
        val date1 = 1747833909779L
        val date2 = 1740833909779L
        val date3 = 1750833909779L

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        val date1String = date1.toStringDateTime()
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"))
        val date2String = date2.toStringDateTime()
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
        val date3String = date3.toStringDateTime()

        assertEquals("2025/05/21 13:25", date1String)
        assertEquals("2025/03/01 14:58", date2String)
        assertEquals("2025/06/24 23:45", date3String)
    }
}