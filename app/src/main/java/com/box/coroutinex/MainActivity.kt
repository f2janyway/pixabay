package com.box.coroutinex

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val WRITE_PERMISSION = 100

    lateinit var photoFragment: PhotoFragment

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == WRITE_PERMISSION) {
            Log.e("fragments", supportFragmentManager.fragments.toString())
            (supportFragmentManager.fragments.last() as ScreenSlideScreenFragment).saveImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        //search_menu.xml 등록
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_list, menu)
        val searchView = menu.findItem(R.id.action_search)
//        Log.e("actionView", searchView.actionView.toString())
        //menuItem을 이용해서 SearchView 변수 생성
        val sv = searchView.actionView as androidx.appcompat.widget.SearchView
        //확인버튼 활성화
        sv.isSubmitButtonEnabled = true

        //SearchView의 검색 이벤트
        sv.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            //검색버튼을 눌렀을 경우
            override fun onQueryTextSubmit(query: String): Boolean {
                photoFragment.doSearch(query)
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view: View? = currentFocus
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = View(this@MainActivity)
                }
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                return true
            }

            //텍스트가 바뀔때마다 호출
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        photoFragment = PhotoFragment.newInstance(getString(R.string.pixa_key), "car")

        supportFragmentManager.beginTransaction()
            .add(R.id.container, photoFragment).commit()
    }

    override fun onBackPressed() {
        if (pager_container.visibility == View.VISIBLE) {
            pager_container.visibility = View.GONE
        } else
            super.onBackPressed()
    }

    val time = MutableLiveData<Long>()

    companion object {
        var job: Job? = null
    }

    fun applyNotification() {
        createNotificationChannel()
        val notificationManagerCompat =
            NotificationManagerCompat.from(this)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "CHANNEL_ID")
        builder.apply {
            setSmallIcon(R.drawable.ic_get_app_white_48dp)
            setContentTitle("time")
            setContentText(System.currentTimeMillis().toString())
            setAutoCancel(true)
            setDeleteIntent(getDeleteIntent())
            priority = NotificationCompat.PRIORITY_DEFAULT
            val startTime =  realTimeToString()
            job = GlobalScope.launch(Dispatchers.Main) {
                while (true) {
                    delay(1000)
                    builder.setContentText(
                         startTime +" " +realTimeToString()
                    )
                    Log.e("while",realTimeToString())
                    notificationManagerCompat.notify(1, builder.build())
                }
            }
            job!!.start()
        }
    }

    fun getDeleteIntent(): PendingIntent {
        val delIntent = Intent(this, NotificationReceiver::class.java)
        delIntent.action = "cancel"
        return PendingIntent.getBroadcast(this, 10, delIntent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    fun realTimeToString(): String {
        val time = System.currentTimeMillis();

		val dayTime =  SimpleDateFormat("hh:mm:ss",Locale.KOREA)


        return dayTime.format(Date(time))
    }

    fun applyCustomNotification() {
        createNotificationChannel()
        //notification manager
        val notificationManagerCompat =
            NotificationManagerCompat.from(this)
        //inflating the views (custom_normal.xml and custom_expanded.xml)
        val remoteCollapsedViews =
            RemoteViews(packageName, R.layout.custom_normal)
        val remoteExpandedViews =
            RemoteViews(packageName, R.layout.custom_expanded)

        //start this(MainActivity) on by Tapping notification
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val mainPIntent = PendingIntent.getActivity(
            this, 0,
            mainIntent, PendingIntent.FLAG_ONE_SHOT
        )

        //creating notification
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "CHANNEL_ID")
        //icon
        builder.setSmallIcon(R.drawable.ic_get_app_white_48dp)
        //set priority
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        //dismiss on tap
        builder.setAutoCancel(true)
        //start intent on notification tap (MainActivity)
        builder.setContentIntent(mainPIntent)
        //custom style
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.setCustomContentView(remoteCollapsedViews)
        builder.setCustomBigContentView(remoteExpandedViews)
        time.value = 1
        val job = GlobalScope.launch(Dispatchers.Main) {
            for (i in 0..100) {
                delay(1000)
                remoteExpandedViews.setTextViewText(
                    R.id.custom_expend_text_info,
                    i.toString()
                )
                notificationManagerCompat.notify(1, builder.build())
            }
        }
        job.start()

        remoteExpandedViews.setTextViewText(
            R.id.custom_expend_text_info,
            time.value.toString()
        )
        //notification manager
        notificationManagerCompat.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "My Notification"
            val description = "My notification description"
            //importance of your notification
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel =
                NotificationChannel("CHANNEL_ID", name, importance)
            notificationChannel.description = description
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
