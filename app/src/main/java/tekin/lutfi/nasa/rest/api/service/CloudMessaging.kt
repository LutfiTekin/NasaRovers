package tekin.lutfi.nasa.rest.api.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.ui.activity.CHANNEL_GENERAL
import tekin.lutfi.nasa.rest.api.ui.activity.Home
import tekin.lutfi.nasa.rest.api.ui.fragment.CURIOSITY
import tekin.lutfi.nasa.rest.api.ui.fragment.OPPORTUNITY
import tekin.lutfi.nasa.rest.api.ui.fragment.SPIRIT
import tekin.lutfi.nasa.rest.api.util.toConsole
import java.util.*

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class CloudMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.shouldOverride())
            return
        super.onMessageReceived(remoteMessage)
    }


    /**
     * Incoming push notification will be overridden
     * if remote message data contains a rover
     */
    private fun RemoteMessage.shouldOverride(): Boolean{
        val destination = data["dest"] ?: return false

        if (arrayOf(CURIOSITY, OPPORTUNITY, SPIRIT).contains(destination)){
            val destinationId = when(destination){
                OPPORTUNITY -> R.id.navigation_opportunity
                SPIRIT -> R.id.navigation_spirit
                else -> R.id.navigation_curiosity
            }
            return sendNotification(destinationId)
        }

        return false
    }

    /**
     * Show overridden message to user
     */
    private fun RemoteMessage.sendNotification(destinationId: Int): Boolean {
        return try {
            val context = this@CloudMessaging
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            val pendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(destinationId)
                //If not specified
                // pending intent routes to default/launcher activity
                // which is not Home
                .setComponentName(Home::class.java)
                .createPendingIntent()
            builder.setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentTitle(this.notification?.title
                    ?: throw Exception("Message title cannot be found"))
                .setContentText(this.notification?.body
                    ?: throw Exception("Message body cannot be found"))
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle().bigText(this.notification?.body.orEmpty()))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)

            val notification = builder.build()
            notificationManager.notify(Random().nextInt(100), notification)
            true
        } catch (e: Exception) {
            e.toConsole()
            false
        }
    }

}