package com.ivantrykosh.app.zeitzuheiraten.domain.model

import android.os.Parcel
import android.os.Parcelable

data class ReportUser(
    val id: String = "",
    val userIdWhoReport: String = "",
    val reportedUserId: String = "",
    val dateTime: Long = 0,
    val description: String = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userIdWhoReport)
        parcel.writeString(reportedUserId)
        parcel.writeLong(dateTime)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReportUser> {
        override fun createFromParcel(parcel: Parcel): ReportUser {
            return ReportUser(parcel)
        }

        override fun newArray(size: Int): Array<ReportUser?> {
            return arrayOfNulls(size)
        }
    }
}
