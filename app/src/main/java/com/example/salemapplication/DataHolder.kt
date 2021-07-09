package com.example.salemapplication

import android.provider.ContactsContract
import com.backendless.BackendlessUser
import java.util.ArrayList

class DataHolder {

    companion object {
        private val dataHolder : DataHolder = DataHolder()
    }

    private lateinit var friends : ArrayList<BackendlessUser>

    fun setFriends(friends : ArrayList<BackendlessUser> ) {
        this.friends = friends
    }






}