package com.example.myapplication.classes

data class User (
    var firstname: String="",
    var lastname: String="",
    var email: String="",
    var preferences: ArrayList<String>? = ArrayList(),
)