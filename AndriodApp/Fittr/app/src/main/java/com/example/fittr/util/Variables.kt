package com.example.fittr.util

object Variables {
    val url = "http://192.168.0.206:8080"
    var userId:Int? = null
    var email : String? = null
    var password : String? = null

    fun setUserId(userId : Int)
    {
        this.userId = userId
    }

}