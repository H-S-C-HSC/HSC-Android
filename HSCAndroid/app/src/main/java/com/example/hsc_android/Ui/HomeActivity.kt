package com.example.hsc_android.network.Ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock.sleep
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.hsc_android.R
import com.example.hsc_android.network.network.HscConnector
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val frag = VoiceFragment()
        val frag2 = PhotoFragment()

        CoroutineScope(Dispatchers.IO).launch {
            while(true){
                val response = HscConnector.getAPI().isthere()
                response.body()?.let{
                    if(it.yes) {
                        push()
                        val resultImage = HscConnector.getAPI().lastPhoto()
                        resultImage.body()?.let {
                            val bitmap = Glide.with(this@HomeActivity).asBitmap().load(it.photo.replace("https", "http")).submit()
                            frag.setImage(bitmap.get())
                        }
                    }

                }
                sleep(1000)
            }
        }
        home_navigationView.setOnNavigationItemSelectedListener {
            when(it.title){
                "음성" -> supportFragmentManager.beginTransaction().replace(R.id.home_fragment, frag).commit()
                "사진" -> supportFragmentManager.beginTransaction().replace(R.id.home_fragment, frag2).commit()
            }
            return@setOnNavigationItemSelectedListener true
        }
        supportFragmentManager.beginTransaction().replace(R.id.home_fragment, frag).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    fun push() {
        var title = "외부인 접근 경고"
        var content = "현재 외부인이 접근을 시도합니다."

        var builder = NotificationCompat.Builder(this, "test")
            .setContentTitle(title).setContentText(content)
            .setSmallIcon(R.drawable.icon_camera)
            .setAutoCancel(true)
            .setShowWhen(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .apply {
                priority = NotificationCompat.PRIORITY_HIGH
            }

        val intent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder.setContentIntent(pendingIntent)

        val ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
            this,
            RingtoneManager.TYPE_NOTIFICATION
        )
        builder.setSound(ringtoneUri)

        builder.addAction(R.mipmap.ic_launcher, "취소", pendingIntent)
        builder.addAction(R.mipmap.ic_launcher, "확인", pendingIntent)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("test","name",importance).apply {
                description = "info"
            }

            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, builder.build())

    }

}