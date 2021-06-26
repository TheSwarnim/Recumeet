package com.hackathon.recumeet.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hackathon.recumeet.R
import com.hackathon.recumeet.chat.adapter.UsersAdapter
import com.hackathon.recumeet.databinding.ActivityUsersBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryUsersRequest
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding

    private val usersAdapter by lazy { UsersAdapter(this) }
    private val client = ChatClient.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        queryAllUsers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.users_menu, menu)
        val search = menu?.findItem(R.id.menu_search)
        val searchView = search?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query!!.isEmpty()) {
                    queryAllUsers()
                } else {
                    searchUser(query)
                }
                return true
            }
        })
        searchView?.setOnCloseListener {
            queryAllUsers()
            false
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            this.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = usersAdapter
    }

    private fun searchUser(query: String) {
        val filters = Filters.and(
            Filters.autocomplete("id", query),
            Filters.ne("id", client.getCurrentUser()!!.id)
        )
        val request = QueryUsersRequest(
            filter = filters,
            offset = 0,
            limit = 100
        )
        client.queryUsers(request).enqueue { result ->
            if (result.isSuccess) {
                val users: List<User> = result.data()
                usersAdapter.setData(users)
            } else {
                Log.e("UsersFragment", result.error().message.toString())
            }
        }
    }

    private fun queryAllUsers() {
        val request = QueryUsersRequest(
            filter = Filters.ne("id", client.getCurrentUser()!!.id),
            offset = 0,
            limit = 100
        )
        client.queryUsers(request).enqueue { result ->
            if (result.isSuccess) {
                val users: List<User> = result.data()
                usersAdapter.setData(users)
            } else {
                Log.e("UsersFragment", result.error().message.toString())
            }
        }
    }
}