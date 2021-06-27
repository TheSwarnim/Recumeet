package com.hackathon.recumeet.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hackathon.recumeet.FireClass
import com.hackathon.recumeet.chat.ChatActivity
import com.hackathon.recumeet.chat.UsersActivity
import com.hackathon.recumeet.databinding.FragmentChannelBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

class ChannelFragment : Fragment() {
    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!

    private val client = ChatClient.instance()
    private lateinit var user: User

    private lateinit var fireClass: FireClass

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =FragmentChannelBinding.inflate(inflater, container, false)
        val view = binding.root

        fireClass = FireClass()
        setUpUser()


        return view
    }

    private fun setUpUser() {
        if (client.getCurrentUser() == null) {
            binding.channelFragProgressbar.visibility = View.VISIBLE

            val ref = Firebase.database.getReference("users").child(fireClass.uid)
            ref.get()
                .addOnSuccessListener {
                    val name = it.child("FName").value.toString() +
                            " " +
                            it.child("LName").value.toString()
                    val profileUri = it.child("ProfileUri").value.toString()

                    user = User(
                        id = fireClass.uid,
                        extraData = mutableMapOf(
                            "name" to name,
                            "image" to profileUri
                        )
                    )

                    val token = client.devToken(user.id)
                    client.connectUser(
                        user = user,
                        token = token
                    ).enqueue { result ->
                        if (result.isSuccess) {
                            Log.d("ChannelFragment", "Success Connecting the User")
                        } else {
                            Log.d("ChannelFragment", result.error().message.toString())
                        }
                    }
                    binding.channelFragProgressbar.visibility = View.GONE

                    setupChannels()

                    binding.channelsView.setChannelDeleteClickListener { channel ->
                        deleteChannel(channel)
                    }

                    binding.channelListHeaderView.setOnActionButtonClickListener {
                        val intent = Intent(view?.context, UsersActivity::class.java)
                        startActivity(intent)
                    }

                    binding.channelsView.setChannelItemClickListener { channel ->
                        val intent = Intent(view?.context, ChatActivity::class.java)
                        intent.putExtra("CHANNEL_ID", channel.cid)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener { OnFailureListener{
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    binding.channelFragProgressbar.visibility = View.GONE
            }   }
        } else{
            setupChannels()

            binding.channelsView.setChannelDeleteClickListener { channel ->
                deleteChannel(channel)
            }

            binding.channelListHeaderView.setOnActionButtonClickListener {
                val intent = Intent(view?.context, UsersActivity::class.java)
                startActivity(intent)
            }

            binding.channelsView.setChannelItemClickListener { channel ->
                val intent = Intent(view?.context, ChatActivity::class.java)
                intent.putExtra("CHANNEL_ID", channel.cid)
                startActivity(intent)
            }
        }
    }

    private fun setupChannels() {
        val filters = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(client.getCurrentUser()!!.id))
        )
        val viewModelFactory = ChannelListViewModelFactory(
            filters,
            ChannelListViewModel.DEFAULT_SORT
        )
        val listViewModel: ChannelListViewModel by viewModels { viewModelFactory }
        val listHeaderViewModel: ChannelListHeaderViewModel by viewModels()

        listHeaderViewModel.bindView(binding.channelListHeaderView, viewLifecycleOwner)
        listViewModel.bindView(binding.channelsView, viewLifecycleOwner)
    }

    private fun deleteChannel(channel: Channel) {
        ChatDomain.instance().deleteChannel(channel.cid).enqueue { result ->
            if (result.isSuccess) {
                showToast("Channel: ${channel.name} removed!")
            } else {
                Log.e("ChannelFragment", result.error().message.toString())
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}