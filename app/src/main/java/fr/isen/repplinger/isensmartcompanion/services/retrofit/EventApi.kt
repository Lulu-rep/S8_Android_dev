package fr.isen.repplinger.isensmartcompanion.services.retrofit

import fr.isen.repplinger.isensmartcompanion.models.EventModel
import retrofit2.Call
import retrofit2.http.GET

interface EventApi {
    @GET("events.json")
    fun getEvents(): Call<List<EventModel>>
}