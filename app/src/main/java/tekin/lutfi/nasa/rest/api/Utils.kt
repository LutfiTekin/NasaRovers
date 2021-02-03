package tekin.lutfi.nasa.rest.api

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


const val NASA_DATE_FORMAT = "yyyy-M-d"

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
private val Context.defaultOkHttpClient: OkHttpClient
    get() {
        val httpCacheDirectory = File(cacheDir, "rf_cache")
        val cacheSize: Long = 20 * 1024 * 1024 // 20 MB
        val cache = Cache(httpCacheDirectory, cacheSize)
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(onlineInterceptor)
            .addNetworkInterceptor(offlineInterceptor)
            .cache(cache)
        return builder
            .build()
    }

@Suppress("DEPRECATION")
val Context.isInternetAvailable: Boolean
    get() {
        return try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            netInfo != null && netInfo.isConnectedOrConnecting
        } catch (e: Exception) {
            false
        }

    }


private val serviceHost: String
    get() {
        return Firebase.remoteConfig.getString("service_base_url_host")
    }

private val apiEndpoint: String
    get() {
        return Firebase.remoteConfig.getString("service_base_url_api")
    }

private val apiKey: String
    get() {
        return Firebase.remoteConfig.getString("service_api_key")
    }

/**
 * Supply every request with api key query parameter
 */
private val apiKeyInterceptor = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val url: HttpUrl = request.url.newBuilder().addQueryParameter("api_key", apiKey).build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}

private val onlineInterceptor =  object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val maxAge = 300 // read from cache for 300 seconds even if there is internet connection
        return response.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAge")
            .removeHeader("Pragma")
            .build()
    }
}

private val Context.offlineInterceptor: Interceptor
    get() = object : Interceptor{
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val maxStale = 60 * 60 * 12 //When offline read from cache up to 12 hours
            if (isInternetAvailable.not())
                request.newBuilder().header(
                    "Cache-Control",
                    "public, only-if-cached, max-stale=$maxStale"
                ).build()
            return chain.proceed(request)
        }
    }

private val serviceBaseUrl: String
    get() {
        return "https://$serviceHost$apiEndpoint"
    }

/**
 * Default Retrofit2 client
 */
val Context.defaultRetrofit: Retrofit
    get() = Retrofit.Builder()
        .baseUrl(serviceBaseUrl)
        .client(defaultOkHttpClient)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().serializeNulls().create()
            )
        )
        .build()


/**
 * Send given object to logcat and selectively Crashlytics
 */
fun Any?.toConsole(omitFromCrashlytics: Boolean = false){
    if (BuildConfig.DEBUG){
        Log.d("NRDLOG", this.toString())
    }
    if (omitFromCrashlytics) return
    val crashlytics = FirebaseCrashlytics.getInstance()
    if (this is Exception)
        crashlytics.recordException(this)
    else  crashlytics.log(this.toString())

}