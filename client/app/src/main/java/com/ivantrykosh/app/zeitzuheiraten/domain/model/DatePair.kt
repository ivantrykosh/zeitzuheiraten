package com.ivantrykosh.app.zeitzuheiraten.domain.model

import android.os.Parcel
import android.os.Parcelable

data class DatePair(
    val startDate: Long = 0,
    val endDate: Long = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(startDate)
        parcel.writeLong(endDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DatePair> {
        override fun createFromParcel(parcel: Parcel): DatePair {
            return DatePair(parcel)
        }

        override fun newArray(size: Int): Array<DatePair?> {
            return arrayOfNulls(size)
        }
    }
}
