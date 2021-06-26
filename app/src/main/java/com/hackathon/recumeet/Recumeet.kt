package com.hackathon.recumeet

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.livedata.ChatDomain

class Recumeet : Application() {

    override fun onCreate() {
        super.onCreate()
        val client =
            ChatClient.Builder(getString(R.string.api_key), this).logLevel(ChatLogLevel.ALL).build()
        ChatDomain.Builder(client, this).build()

        Firebase.database.setPersistenceEnabled(true)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}