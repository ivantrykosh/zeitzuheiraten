package com.ivantrykosh.app.zeitzuheiraten.domain.model

import android.os.Parcel
import android.os.Parcelable

data class DisplayedChat(
    val id: String = "",
    val withUserId: String = "",
    val withUsername: String = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(withUserId)
        parcel.writeString(withUsername)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DisplayedChat> {
        override fun createFromParcel(parcel: Parcel): DisplayedChat {
            return DisplayedChat(parcel)
        }

        override fun newArray(size: Int): Array<DisplayedChat?> {
            return arrayOfNulls(size)
        }
    }
}
