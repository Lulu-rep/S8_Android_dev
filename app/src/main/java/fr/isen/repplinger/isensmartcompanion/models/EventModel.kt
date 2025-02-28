package fr.isen.repplinger.isensmartcompanion.models

import java.io.Serializable

data class EventModel
    (
    var id: String,
    var title: String,
    var description: String,
    var date: String,
    var location: String,
    var category: String,
    var isPinned: Boolean = false
): Serializable
