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
        setupChannels()

        binding.channelsView.setChannelDeleteClickListener { channel ->
            deleteChannel(channel)
        }

        binding.channelListHeaderView.setOnActionButtonClickListener {
//            val action = ChannelFragmentDirections.actionChannelFragmentToUsersFragment()
            val intent = Intent(view.context, UsersActivity::class.java)
            startActivity(intent)
//            findNavController().navigate(R.id.action_channelFragment_to_usersFragment)
//            findNavController().navigate(action)
        }

        binding.channelsView.setChannelItemClickListener { channel ->
//            val action = ChannelFragmentDirections.actionChannelFragmentToChatFragment(channel.cid)
//            findNavController().navigate(action)
            val intent = Intent(view.context, ChatActivity::class.java)
            intent.putExtra("CHANNEL_ID", channel.cid)
            startActivity(intent)
        }

        return view
    }

    private fun setUpUser() {
        var name = "Swarnim"
        try{
            name = fireClass.name
        } catch (e : Exception){
            Log.i("Channel Fragment Exception", e.toString())
        }
        Log.i("Channel Fragment Name", name)
        showToast(name)

        if (client.getCurrentUser() == null) {
            user = User(
                id = fireClass.uid,
                extraData = mutableMapOf(
                    "name" to name
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