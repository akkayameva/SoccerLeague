package com.akkayameva.soccerleague.data

import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class Apifactory {

    //Creating Auth Interceptor to add api_key query in front of all the requests.
    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url
            .newBuilder()
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    //OkhttpClient for building http request url
    private val tmdbClient = OkHttpClient().newBuilder()
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    var moshi = Moshi.Builder().build()

    fun retrofit(): Retrofit = Retrofit.Builder()
        .client(tmdbClient)

            //Team Test, size 5
       // .baseUrl("https://2a209f42-d6ba-451f-90d4-44c94bba4f2b.mock.pstmn.io")

            //All Teams, size 21
        .baseUrl("https://98391657-efda-4af0-8973-33c99e1f12ed.mock.pstmn.io")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()


    val api: SoccerApi = retrofit().create(SoccerApi::class.java)

}


