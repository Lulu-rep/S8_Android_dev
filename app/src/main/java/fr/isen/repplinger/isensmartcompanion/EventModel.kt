package fr.isen.repplinger.isensmartcompanion

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class EventModel
    (
    var id: String,
    var title: String,
    var description: String,
    var date: String,
    var location: String,
    var category: String
): Serializable
