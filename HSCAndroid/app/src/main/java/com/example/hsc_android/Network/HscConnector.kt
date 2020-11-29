package com.example.hsc_android.network.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HscConnector{
    private var retrofit: Retrofit
    private var api: HscAPI
    private const val baseURL = "http://172.20.10.3:5000"

    init{
        retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        api = retrofit.create(HscAPI::class.java)
    }

    fun getAPI() = api

}