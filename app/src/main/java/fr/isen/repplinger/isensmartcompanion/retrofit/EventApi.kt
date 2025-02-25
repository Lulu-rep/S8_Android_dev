package fr.isen.repplinger.isensmartcompanion.retrofit

import fr.isen.repplinger.isensmartcompanion.EventModel
import retrofit2.Call
import retrofit2.http.GET

interface EventApi {
    @GET("events.json")
    fun getEvents(): Call<List<EventModel>>
}