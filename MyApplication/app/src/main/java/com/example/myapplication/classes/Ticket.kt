package com.example.myapplication.classes

import android.os.Parcelable
import java.io.Serializable

data class Ticket (
    var title: String="",
    var location: String="",
    var data: String="",
    var urlToImage: String="",
    var details:String="",
    //var numberTickets:Int
)