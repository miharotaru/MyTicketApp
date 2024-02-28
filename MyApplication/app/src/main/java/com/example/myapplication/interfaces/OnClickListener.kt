package com.example.myapplication.interfaces

import com.example.myapplication.classes.Ticket
//aceasta interfata ma ajuta ca sa pot sa dau click pe un singur tichet si sa imi ia
// pozitia ticketului
interface OnClickListener {
    fun onClickListenerDetails(ticketPos: Int)
}