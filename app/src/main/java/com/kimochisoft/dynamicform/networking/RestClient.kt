package com.kimochisoft.dynamicform.networking

import android.content.Context
import com.kimochisoft.dynamicform.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RestClient {
    companion object {
        private const val MAX_TIMEOUT = 45L

        fun create(context: Context): Retrofit {
            val builder = OkHttpClient.Builder()

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)

            builder.connectTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
            builder.readTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
            builder.writeTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)

            val accessToken = Constants.TOKEN

            builder.addInterceptor {chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json; q=0.5")
                    .addHeader("token", accessToken)
                    .build()

                return@addInterceptor chain.proceed(request)
            }

            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.SERVER_PATH).client(builder.build()).build()
        }
    }
}