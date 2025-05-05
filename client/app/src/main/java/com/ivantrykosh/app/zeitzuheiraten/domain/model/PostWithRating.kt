package com.ivantrykosh.app.zeitzuheiraten.domain.model

import android.os.Parcel
import android.os.Parcelable

data class PostWithRating(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val category: String = "",
    val cities: List<String> = emptyList(),
    val description: String = "",
    val minPrice: Int = 0,
    val photosUrl: List<String> = emptyList(),
    val notAvailableDates: List<DatePair> = emptyList(),
    val enabled: Boolean = true,
    val rating: Rating = Rating(0.0, 0),
    val creationTime: Long = 0,
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
        parcel.createTypedArrayList<DatePair>(DatePair.CREATOR)!!,
        parcel.readByte() != 0.toByte(),
        parcel.readTypedObject(Rating.CREATOR)!!,
        parcel.readLong(),
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
        parcel.writeByte(if (enabled) 1 else 0)
        parcel.writeTypedObject(rating, 0)
        parcel.writeLong(creationTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostWithRating> {
        override fun createFromParcel(parcel: Parcel): PostWithRating {
            return PostWithRating(parcel)
        }

        override fun newArray(size: Int): Array<PostWithRating?> {
            return arrayOfNulls(size)
        }
    }
}
