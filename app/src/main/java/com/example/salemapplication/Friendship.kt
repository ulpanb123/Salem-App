package com.example.salemapplication

import com.backendless.BackendlessUser

class Friendship {
    private lateinit var objectId : String
    private lateinit var user1  :  BackendlessUser
    private lateinit var user2 : BackendlessUser

    fun getObjectId() : String {
        return objectId
    }

    fun setObjectTitle(objectId : String) {
        this.objectId = objectId
    }

    fun getUser1() : BackendlessUser {
        if(this::user1.isInitialized) {
            return user1
        }
        return BackendlessUser()
    }

    fun setUser1(user1 : BackendlessUser) {
        this.user1 = user1
    }

    fun getUser2() : BackendlessUser {
        if(this::user2.isInitialized)
            return user2
        return BackendlessUser()
    }

    fun setUser2(user2 : BackendlessUser) {
        this.user2 = user2
    }








}