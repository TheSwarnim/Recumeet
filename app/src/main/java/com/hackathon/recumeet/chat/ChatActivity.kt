package com.hackathon.recumeet.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.hackathon.recumeet.databinding.ActivityChatBinding
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel
import io.getstream.chat.android.ui.message.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory

class ChatActivity : AppCompatActivity() {
    private lateinit var channelId : String

    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        channelId = intent.getStringExtra("CHANNEL_ID").toString()
        setupMessages()

        binding.messagesHeaderView.setBackButtonClickListener {
            this.onBackPressed()
        }
    }

    private fun setupMessages() {
        val factory = MessageListViewModelFactory(cid = channelId)

        val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        messageListHeaderViewModel.bindView(binding.messagesHeaderView, this)
        messageListViewModel.bindView(binding.messageList, this)
        messageInputViewModel.bindView(binding.messageInputView, this)
    }
}