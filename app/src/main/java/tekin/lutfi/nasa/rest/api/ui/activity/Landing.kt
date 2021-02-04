package tekin.lutfi.nasa.rest.api.ui.activity

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import tekin.lutfi.nasa.rest.api.R

const val CHANNEL_GENERAL = "general"

const val DEEP_LINK_DESTINATION = "dest"
const val CUSTOM_URI_SCHEME = "nasarover://"

class Landing : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        createNotificationChannels()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        Firebase.remoteConfig.setConfigSettingsAsync(configSettings)
        Firebase.remoteConfig.setDefaultsAsync(R.xml.remote_config).addOnCompleteListener {
            val home = Intent(this, Home::class.java)
            if (intent.extras != null){
                val destinationRover = intent.getStringExtra(DEEP_LINK_DESTINATION)
                home.data = Uri.parse("$CUSTOM_URI_SCHEME$destinationRover")
            }
            startActivity(home)
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        //Notification channels are only available in android O(8.0) or higher
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ntfGeneral = NotificationChannel(
            CHANNEL_GENERAL,
            getString(R.string.notification_channel_general),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        // Configure the notification channel.
        ntfGeneral.description = getString(R.string.notification_channel_general_description)
        ntfGeneral.lightColor = Color.RED
        ntfGeneral.vibrationPattern = longArrayOf(400, 300, 200, 400)
        ntfGeneral.enableLights(true)
        ntfGeneral.setShowBadge(true)
        ntfGeneral.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes)
        ntfGeneral.enableVibration(true)
        mNotificationManager.createNotificationChannel(ntfGeneral)

    }

}