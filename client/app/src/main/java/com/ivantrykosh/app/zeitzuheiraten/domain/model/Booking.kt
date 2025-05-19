package com.ivantrykosh.app.zeitzuheiraten.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingStatus

data class Booking(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val postId: String = "",
    val category: String = "",
    val providerId: String = "",
    val provider: String = "",
    val dateRange: DatePair = DatePair(),
    val status: BookingStatus = BookingStatus.NOT_CONFIRMED,
    val creatingDateTime: Long = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readTypedObject<DatePair>(DatePair.CREATOR)!!,
        BookingStatus.stringToValue(parcel.readString()!!),
        parcel.readLong(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(postId)
        parcel.writeString(category)
        parcel.writeString(providerId)
        parcel.writeString(provider)
        parcel.writeTypedObject(dateRange, 0)
        parcel.writeString(status.name)
        parcel.writeLong(creatingDateTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Booking> {
        override fun createFromParcel(parcel: Parcel): Booking {
            return Booking(parcel)
        }

        override fun newArray(size: Int): Array<Booking?> {
            return arrayOfNulls(size)
        }
    }
}
