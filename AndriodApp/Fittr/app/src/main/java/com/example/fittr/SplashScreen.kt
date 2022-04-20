package com.example.fittr

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.fittr.authentication.Login


class SplashScreen : AppCompatActivity(){



    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 1000 //1 seconds

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_splashscreen)
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val darkModePref = preferences.getInt("DarkMode",-10)

        var x = (0..2).shuffled().last()
        val listOfQuotes = arrayOf("Ride as much or as little, as long or as short as you feel. But ride -Eddy Merckx",
            "A bicycle ride around the world begins with a single pedal stroke – Scott Stoll",
            "The race is won by the rider who can suffer the most” – Eddy Merckx")
        val quotes = findViewById<TextView>(R.id.textView3)

        quotes.text = listOfQuotes[x]

        if(darkModePref!=-10)
        {
            when(darkModePref)
            {
                0-> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)}
                1-> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)}
                else -> {}
            }
        }



        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

    }
}