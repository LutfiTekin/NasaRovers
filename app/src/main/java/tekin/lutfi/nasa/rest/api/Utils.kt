package tekin.lutfi.nasa.rest.api

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val DEFAULT_TIMEOUT: Long = 15000

/**
 * Interceptor for okHttpClient
 */
fun loggingInterceptor(logLevel: HttpLoggingInterceptor.Level): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) logLevel else HttpLoggingInterceptor.Level.NONE
    }
}

/**
 * Default okHttpClient used for Retrofit2
 */
val defaultOkHttpClient: OkHttpClient
    get() {
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .addInterceptor(loggingInterceptor(HttpLoggingInterceptor.Level.BODY))
        return builder
            .build()
    }


private val serviceHost: String
    get() {
        return Firebase.remoteConfig.getString("service_base_url_host")
    }

private val apiEndpoint: String
    get() {
        return Firebase.remoteConfig.getString("service_base_url_api")
    }

private val serviceBaseUrl: String
    get() {
        return "https://$serviceHost$apiEndpoint"
    }

/**
 * Default Retrofit2 client
 */
val defaultRetrofit: Retrofit
    get() = Retrofit.Builder()
        .baseUrl(serviceBaseUrl)
        .client(defaultOkHttpClient)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().serializeNulls().create()
            )
        )
        .build()