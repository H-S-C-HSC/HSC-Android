package com.example.hsc_android.network.network

import com.example.hsc_android.network.Data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface HscAPI {

    @GET("/photo/isthere")
    suspend fun isthere(
    ): Response<isthere>

    @GET("/alert/photo/lastest")
    suspend fun lastPhoto(
    ): Response<lastPhoto>

    @GET("/alert/photo")
    suspend fun getList(
    ): Response<ArrayList<photo>>

    @POST("/voice-response/{id}")
    suspend fun choiVoice(
        @Path("id") id: Int
    ): Response<Status>

    @GET("/voice-response")
    suspend fun voiceList(): Response<voiceResponse>

    @Multipart
    @POST(" /voice-response")
    suspend fun saveVoice(
        @Part title: MultipartBody.Part,
        @Part voice: MultipartBody.Part
    ) : Response<Unit>

}