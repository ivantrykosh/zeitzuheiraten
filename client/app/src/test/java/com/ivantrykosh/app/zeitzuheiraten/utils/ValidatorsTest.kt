package com.ivantrykosh.app.zeitzuheiraten.utils

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ValidatorsTest {

    @Test
    fun `valid email returns true`() {
        val email = "test@example.com"
        assertTrue(isEmailValid(email))
    }

    @Test
    fun `invalid email returns false`() {
        val email = "invalid-email"
        assertFalse(isEmailValid(email))
    }

    @Test
    fun `valid password returns true`() {
        val password = "Password1"
        assertTrue(isPasswordValid(password))
    }

    @Test
    fun `invalid password returns false`() {
        val password = "pass" // too short, no digit or capital
        assertFalse(isPasswordValid(password))
    }

    @Test
    fun `valid name returns true`() {
        val name = "Alice"
        assertTrue(isNameValid(name))
    }

    @Test
    fun `blank name returns false`() {
        val name = "   "
        assertFalse(isNameValid(name))
    }

    @Test
    fun `file size within limit returns true`() {
        val uri = mock<Uri>()
        val cursor = mock<Cursor>()
        val contentResolver = mock<ContentResolver>()

        whenever(cursor.getColumnIndex(OpenableColumns.SIZE)).thenReturn(0)
        whenever(cursor.moveToFirst()).thenReturn(true)
        whenever(cursor.getLong(0)).thenReturn(Constants.MAX_FILE_SIZE - 1L)
        whenever(contentResolver.query(eq(uri), eq(null), eq(null), eq(null), eq(null))).thenReturn(cursor)

        val result = isFileSizeAppropriate(uri, contentResolver)
        assertTrue(result)
        verify(cursor).close()
    }

    @Test
    fun `file size exceeds limit returns false`() {
        val uri = mock<Uri>()
        val cursor = mock<Cursor>()
        val contentResolver = mock<ContentResolver>()

        whenever(cursor.getColumnIndex(OpenableColumns.SIZE)).thenReturn(0)
        whenever(cursor.moveToFirst()).thenReturn(true)
        whenever(cursor.getLong(0)).thenReturn(Constants.MAX_FILE_SIZE + 1L)
        whenever(contentResolver.query(eq(uri), eq(null), eq(null), eq(null), eq(null))).thenReturn(cursor)

        val result = isFileSizeAppropriate(uri, contentResolver)
        assertFalse(result)
        verify(cursor).close()
    }
}