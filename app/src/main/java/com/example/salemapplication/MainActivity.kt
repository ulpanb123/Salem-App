package com.example.salemapplication

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.BackendlessDataQuery
import com.backendless.persistence.DataQueryBuilder
import com.backendless.persistence.QueryOptions
import com.example.salemapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG  = "MainActivity"
    private lateinit var addFriendButton : Button
    private lateinit var friendsListView : ListView
    private lateinit var friends : ArrayList<BackendlessUser>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        addFriendButton = binding.addFriendButton
        friendsListView = binding.friendsListView

        addFriendButton.setOnClickListener {
            onAddFriendClick()
        }
        getFriends()
    }

    private fun onAddFriendClick() {
        val alert : AlertDialog.Builder = AlertDialog.Builder(this)
        val editText : EditText = EditText(this)
        alert.setMessage("Add Friend")
        alert.setTitle("Enter email")
        alert.setView(editText)

        alert.setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
            val email : String = editText.text.toString()
            addFriend(email)
        })

        alert.setNegativeButton("Click", DialogInterface.OnClickListener { dialog, which ->
            //do nothing
        })

        alert.show()
    }

    private fun addFriend(email: String) {
        val query : BackendlessDataQuery = BackendlessDataQuery()
        query.whereClause = "email = '$email'"
        val queryBuilder = DataQueryBuilder.create()

        Backendless.Data.of(BackendlessUser::class.java).find(queryBuilder, object : AsyncCallback<List<BackendlessUser>> {
            override fun handleFault(fault: BackendlessFault?) {
                if (fault != null) {
                    Log.e(TAG, "failed to get user " + fault.message)
                }
            }

            override fun handleResponse(response: List<BackendlessUser>?) {
                Log.d(TAG, "found users are : $response")
                if (response != null) {
                    if(response.isNotEmpty()) {
                            addFriend(response[0])
                    }
                }
            }
        })

    }

    private fun addFriend(friendUser: BackendlessUser) {
        Log.e(TAG, "loooooooooop")
        val friendship = Friendship()
        val currUser = Backendless.UserService.CurrentUser()
        Log.e(TAG, "loooooooooop")
        friendship.setUser1(currUser)
        friendship.setUser2(friendUser)

        Backendless.Data.of(Friendship::class.java).save(friendship, object : AsyncCallback<Friendship> {
            override fun handleFault(fault: BackendlessFault?) {
                if (fault != null) {
                    Log.e(TAG, "failed to save friendship relationship " + fault.message)
                }
            }

            override fun handleResponse(response: Friendship?) {
                Log.d(TAG, "successfully saved friendship")
                getFriends()
            }
        })

    }

    private fun getFriends() {
        //will download all friendship relationships

        val user  = Backendless.UserService.CurrentUser()
        val userId = user.objectId
        val whereClause = "user1.objectId = '$userId' OR user2.objectId = '$userId'"

        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause

        val queryOptions = QueryOptions()
        queryOptions.addRelated("user1")
        queryOptions.addRelated("user2")
        queryBuilder.addSortBy(queryOptions.toString())

        Backendless.Data.of(Friendship::class.java).find(queryBuilder, object : AsyncCallback<List<Friendship>>{
            override fun handleFault(fault: BackendlessFault?) {
                if (fault != null) {
                    Log.e(TAG, "couldn't download friendships: " + fault.message)
                }
            }

            override fun handleResponse(response: List<Friendship>?) {
                Log.d(TAG, "got friends: $response")
                val friends = ArrayList<BackendlessUser>()
                val currUser = Backendless.UserService.CurrentUser()

                if (response != null) {
                    Log.d(TAG, "LOOOOOOOL")
                    for(friendship : Friendship in response) {
                        Log.d(TAG, "LOOOOOOOL")
                        if(friendship.getUser1().objectId == currUser.objectId) {
                            Log.d(TAG, "1st")
                            friends.add(friendship.getUser2())
                        } else {
                            Log.d(TAG, "2nd")
                            friends.add(friendship.getUser1())
                        }
                        Log.d(TAG, "YES")
                    }
                }
                displayFriends(friends)
                Log.d(TAG, "YEs2")
            }
        })
    }

    private fun displayFriends(friends : ArrayList<BackendlessUser>) {
        val dataHolder = DataHolder()
        dataHolder.setFriends(friends)
        this.friends = friends

        val adapter  = FriendsAdapter(friends, this)
        friendsListView.adapter = adapter

    }

    override fun onDestroy() {
        Log.d(TAG, "on destroy called")
        super.onDestroy()
    }
}