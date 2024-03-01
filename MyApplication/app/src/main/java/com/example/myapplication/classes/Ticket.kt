package com.example.myapplication.classes

import android.os.Parcelable
import java.io.Serializable

data class Ticket (
    var title: String="",
    var location: String="",
    var city:String="",
    var data: String="",
    var urlToImage: String="",
    var details:String="",
    var numberTickets:Int=-1,
    var priceCategoryOne: Int=-1,
    var priceCategoryTwo: Int=-1,
    var priceCategoryThree: Int=-1,
    var priceCategoryVIP: Int=-1,
    var category: String="",
)