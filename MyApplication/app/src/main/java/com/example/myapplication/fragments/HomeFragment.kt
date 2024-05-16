package com.example.myapplication.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.DashboardActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.TicketAdapter
import com.example.myapplication.classes.Ticket
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.interfaces.OnClickListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson


const val CHANNEL_ID = "channelId"

class HomeFragment : Fragment(), OnClickListener {
    private var ticketList: ArrayList<Ticket>? = ArrayList()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var adapter: TicketAdapter
   // private lateinit var ticket:Ticket
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFirebase()
//        ticket=Ticket("Titlul","locatiom","city","12121",
//            "detalii",12,12,12,
//            12,12,"dsa")
        //setNotification(ticket)

        //notificationTest()

        isNotificationOn()
    }

    private fun notificationTest(){
//        val someIntValue = 1
//
//        context?.let { ctx ->
//            val sharedPreferences: SharedPreferences = ctx.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//
//            val gson = Gson()
//            val ticketJson = gson.toJson(ticket)
//
//            val editor = sharedPreferences.edit()
//            editor.putString("ticketCeva", ticketJson)
//            editor.putInt("someIntValue", someIntValue)
//            editor.apply()
//        }
    }

    private fun isNotificationOn() {
        var someIntValue: Int = -1
        context?.let { ctx ->
            val sharedPreferences: SharedPreferences =
                ctx.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            // Recuperarea JSON-ului din SharedPreferences
            val ticketJson = sharedPreferences.getString("ticketCeva", null)

            // Deserializarea obiectului Ticket
            val gson = Gson()
            val ticket: Ticket? = gson.fromJson(ticketJson, Ticket::class.java)

            someIntValue =
                sharedPreferences.getInt("someIntValue", -1) // -1 este valoarea implicită

            if (someIntValue == 1) {
                if (ticket != null) {

                    setNotification(ticket)
                }else{
                    Log.d("ticket","ticketul e nul :((")
                }
            }

            someIntValue = 0
            val editor = sharedPreferences.edit()
            editor.putInt("someIntValue", someIntValue)
            editor.apply()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificare canal"
            val descriptionText = "Descriere canal de notificare"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = requireActivity().getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setNotification(ticket: Ticket) {
        createNotificationChannel()

        val intent = Intent(context, DashboardActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ticket)
            setContentTitle(ticket.title)
            setContentText(ticket.category)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText("Acesta este textul extins al notificării." +
                    " Are loc la " +ticket.location + " categoria "+ticket.category+
                    "detalii utilizatorului. Textul extins permite afișarea " +
                    "unei cantități mai mari de informații.")


        val builder2 = NotificationCompat.Builder(requireContext(),  CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ticket) // Icon-ul mic
            setContentTitle("Nu rata evenimentul acesta" + ticket.title)
            setContentText("This is a test notification from My App")
            //setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.large_icon)) // Icon-ul mare
            setStyle(bigTextStyle) // Sau folosește bigPictureStyle pentru stilul de imagine mare
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        val notificationManager = NotificationManagerCompat.from(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(
            1,
            builder2.build()
        )  // '1' este ID-ul notificării, asigură-te că este unic // '1' este ID-ul notificării, asigură-te că este unic pentru fiecare notificare
    }

    //luam datele din firebase si le punem in lista de tichete
    private fun getDataFirebase() {
        database = FirebaseFirestore.getInstance()
        database.collection("tickets").addSnapshotListener(object : EventListener<QuerySnapshot> {

            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        ticketList?.add(dc.document.toObject(Ticket::class.java))
                    }
                    Log.d("Firestore error", ticketList?.size.toString())
                }
                initAdapter()
                //nu functioneaza dar il las aici
                adapter.notifyDataSetChanged()
            }

        }
        )
    }

    private fun initAdapter() {

        binding.recycleviewTicketItemFragmentHome.layoutManager =
            LinearLayoutManager(requireContext())
        adapter = TicketAdapter(ticketList as ArrayList<Ticket>, this)
        binding.recycleviewTicketItemFragmentHome.adapter = adapter
        adapter.notifyDataSetChanged()
        Log.d("init adaper", "initadapter ticketList?.size.toString()")
        Log.d("init adaper", ticketList?.size.toString())

    }

    //aici se intampla magia cand se se apasa pe un ticket propriu zis si se transfera datele
    // de la un fragment la altul
    override fun onClickListenerDetails(ticketPos: Int) {
        // Toast.makeText(context, "Hello"+ticketItem.toString()+" ceva", Toast.LENGTH_SHORT).show()
        val detailsFragment = DetailsTicketFragment()
        val bundle = Bundle()
        Log.d("ticketmeu", ticketList?.get(ticketPos).toString())
        bundle.putInt("ticketItem", ticketPos)
        detailsFragment.arguments = bundle

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_main_drawer2, detailsFragment)
        transaction.addToBackStack(null)

        transaction.commit()
        //poate pot sa rezolv bug-ul cu aceasta metoda
        //requireActivity().supportFragmentManager.popBackStack()
    }
}
