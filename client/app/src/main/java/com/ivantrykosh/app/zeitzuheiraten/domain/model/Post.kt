package com.ivantrykosh.app.zeitzuheiraten.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Post(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val category: String = "",
    val cities: List<String> = emptyList(),
    val description: String = "",
    val minPrice: Int = 0,
    val photosUrl: List<String> = emptyList(),
    val notAvailableDates: List<DatePair> = emptyList(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.createStringArrayList()!!,
        parcel.createTypedArrayList<DatePair>(DatePair.CREATOR)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(providerId)
        parcel.writeString(providerName)
        parcel.writeString(category)
        parcel.writeStringList(cities)
        parcel.writeString(description)
        parcel.writeInt(minPrice)
        parcel.writeStringList(photosUrl)
        parcel.writeTypedList<DatePair>(notAvailableDates)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}
