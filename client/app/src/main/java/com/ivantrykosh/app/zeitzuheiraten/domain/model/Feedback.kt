package com.ivantrykosh.app.zeitzuheiraten.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Feedback(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val postId: String = "",
    val rating: Int = 0,
    val description: String = "",
    val date: Long = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readLong(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(postId)
        parcel.writeInt(rating)
        parcel.writeString(description)
        parcel.writeLong(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Feedback> {
        override fun createFromParcel(parcel: Parcel): Feedback {
            return Feedback(parcel)
        }

        override fun newArray(size: Int): Array<Feedback?> {
            return arrayOfNulls(size)
        }
    }
}
