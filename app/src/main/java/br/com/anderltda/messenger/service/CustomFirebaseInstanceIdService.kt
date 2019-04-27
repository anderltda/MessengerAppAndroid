package br.com.anderltda.messenger.service

import android.util.Log
import br.com.anderltda.messenger.domain.TokenEvent
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import org.greenrobot.eventbus.EventBus

class CustomFirebaseInstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token

        val tokenEvent = token?.let { TokenEvent(it) }
        EventBus.getDefault().post(tokenEvent)
        Log.i("Token", token)
    }
}