package com.ivantrykosh.app.zeitzuheiraten.utils

/**
 * Statuses that bookings can have
 */
enum class BookingStatus {
    NOT_CONFIRMED, CONFIRMED, CANCELED, SERVICE_PROVIDED;

    companion object {
        fun stringToValue(name: String): BookingStatus {
            for (entry in entries) {
                if (entry.name == name) {
                    return entry
                }
            }
            throw IllegalArgumentException("Booking status with name $name does not exist")
        }
    }
}