package com.gammasolution.segarmart.util.connection

import android.content.Context
import android.util.Log
import android.widget.Toast
import co.id.distriboost.util.SharedPref
import co.id.tesis.BuildConfig
import co.id.tesis.model.Auth
import co.id.tesis.model.ResponseMacAddress
import co.id.tesis.model.ResponsePresensi
import com.gammasolution.segarmart.model.BasicErrorModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ServiceInterface {
    @FormUrlEncoded
    @POST("api/login")
    fun login(@Field("nip") nip: String,
              @Field("password") password: String,
              @Field("device_id") device_id: String): Observable<Auth>

    @FormUrlEncoded
    @POST("api/absen/day")
    fun getPresentToday(@Field("no_induk") nip: String): Observable<ResponsePresensi>

    @FormUrlEncoded
    @POST("api/absen/range")
    fun getPresentRange(@Field("no_induk") nip: String,
                        @Field("from") from: String,
                        @Field("to") to: String): Observable<ResponsePresensi>

    @FormUrlEncoded
    @POST("api/absen")
    fun postPresent(@Field("no_induk") nip: String,
                    @Field("mac_address") mac_address: String,
                    @Field("tanggal") tanggal: String,
                    @Field("lat") lat: Double,
                    @Field("lng") lng: Double): Observable<BasicErrorModel>

    @GET("api/router")
    fun getMacAddress(): Observable<ResponseMacAddress>

    companion object {
        private val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        var baseUrl: String = "http://192.168.1.26:8000"
        fun create(): ServiceInterface {
//            if (BuildConfig.APPLICATION_ID == "co.id.tesis") {
                val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build()
                return retrofit.create(ServiceInterface::class.java)
//            } else if (BuildConfig.APPLICATION_ID == "co.id.tesis.dev") {
//                val retrofit = Retrofit.Builder()
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .client(client)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .baseUrl(baseUrlDev)
//                    .build()
//                return retrofit.create(ServiceInterface::class.java)
//            }
//            return Retrofit.Builder().build().create(ServiceInterface::class.java)
        }

        fun createAuthenticationHeader(sharedPreferenceHelper: SharedPref): Map<String, String> {
            var headerMap: HashMap<String, String> = HashMap<String, String>()

            headerMap.put("Authorization", sharedPreferenceHelper.getString("token", "-")!!)
            headerMap.put("Accept", "application/json")
            headerMap.put("Content-Type", "application/json")

            return headerMap

        }

        fun createHeader(): Map<String, String> {
            var headerMap: Map<String, String> = mutableMapOf<String, String>()

            headerMap.plus(Pair("Accept", "application/json"))
            headerMap.plus(Pair("Content-Type", "application/x-www-form-urlencoded"))

            return headerMap

        }


        fun handleError(throwable: Throwable, context: Context) {
            try {
                val exception = throwable as HttpException
                val gson = Gson()
                val errorModel = gson.fromJson(
                    exception.response()?.errorBody()?.charStream(),
                    BasicErrorModel::class.java
                )
                Toast.makeText(context, errorModel.message, Toast.LENGTH_SHORT).show()
            } catch (exception: Exception) {
                Log.e("error",exception.message)
                Toast.makeText(context, "Tolong periksa koneksi anda", Toast.LENGTH_SHORT).show()
            }

        }

    }
}