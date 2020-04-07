package com.box.coroutinex.data

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class Repo(val name:String,val full_namr:String,val owner: Owner)
interface APIService {
    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String): Call<List<Repo>>
    @GET("api/")
    fun searchImages(@Query("key") key:String, @Query("q") query: String, @Query("page") page : Int) : Call<Photo>

    companion object {
        //        https://api.github.com/repos/yegyu/DotNet
        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val pixabayRetrofit  =  Retrofit.Builder().baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

//        val retrofitCoffee: Retrofit = Retrofit.Builder().baseUrl(URLs.URL )
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val retrofitImage: Retrofit = Retrofit.Builder().baseUrl("https://images.ctfassets.net")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
    }
}