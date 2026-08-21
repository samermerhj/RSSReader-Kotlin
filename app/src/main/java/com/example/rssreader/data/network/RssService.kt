package com.example.rssreader.data.network

import retrofit2.http.GET
import retrofit2.http.Url

interface RssService {
    @GET
    suspend fun fetchRss(@Url url: String): retrofit2.Response<okhttp3.ResponseBody>
}