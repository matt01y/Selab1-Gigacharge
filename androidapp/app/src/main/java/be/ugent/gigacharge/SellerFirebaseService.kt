package be.ugent.gigacharge

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Override base class methods to handle any events required by the application.
 * All methods are invoked on a background thread, and may be called when the app is in the background or not open.
 *
 *  The registration token may change when:
 *  - The app deletes Instance ID
 *  - The app is restored on a new device
 *  - The user uninstalls/reinstall the app
 *  - The user clears app data.
 */

class SellerFirebaseService
    : FirebaseMessagingService() {


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    var onToken: (String) -> Unit = { token ->
        Log.i("SellerFirebaseService ", "empty token listener :: $token")
    }

    private var token: String? = null

    fun hasToken(): Boolean {
        return token != null
    }


    override fun onNewToken(ntoken: String) {
        super.onNewToken(ntoken)
        Log.i("SellerFirebaseService ", "Refreshed token :: $ntoken")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //onToken(token)
        token = ntoken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.i("SellerFirebaseService ", "Message :: $message")
    }
}