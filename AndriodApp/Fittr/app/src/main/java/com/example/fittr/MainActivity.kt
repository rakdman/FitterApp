package com.example.fittr

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.databinding.ActivityMainBinding
import com.example.fittr.fragments.*
import com.example.fittr.fragments.adapters.ViewPageAdapter
import org.json.JSONObject
import android.view.MotionEvent
import android.view.View

import android.view.View.OnTouchListener
import com.example.fittr.util.Variables


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var coins : Int = 0
    var km : Double = 0.0
    var level : Int = 0

    companion object
    {
        var ins: MainActivity? = null
        fun getInstance() : MainActivity?
        {
            return ins
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        ins = this
        getCoinsAndKM(1)
        setUpTabs()
    }

    private fun setUpTabs()
    {
        val adapter = ViewPageAdapter(supportFragmentManager)
        adapter.addFragment(BikeFragment(),"")
        adapter.addFragment(VouchersFragment(),"")
        adapter.addFragment(ProfileFragment(),"")
        adapter.addFragment(LeaderboardFragment(),"")
        adapter.addFragment(SettingsFragment(),"")
        binding.viewPager.adapter=adapter
        binding.viewPager.offscreenPageLimit = 3
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_accessibility_24)
        binding.tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_voucher_24)
        binding.tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_assignment_ind_24)
        binding.tabs.getTabAt(3)!!.setIcon(R.drawable.ic_baseline_leaderboard_24)
        binding.tabs.getTabAt(4)!!.setIcon(R.drawable.ic_baseline_settings_24)
        
    }

    private fun getCoinsAndKM(userId : Int)
    {
        val queue = Volley.newRequestQueue(applicationContext)
        var url="${Variables.url}/api/user/metrics/${userId}"
        val builder = Uri.parse(url).buildUpon()
        val params = mapOf<String, Any>()
        val jsonObject = JSONObject(params)

        val request = object : JsonObjectRequest(
            Request.Method.GET, builder.toString(), jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                this.km = jsonObj.getDouble("DISTANCE")/1000
                this.coins=jsonObj.getInt("COINS")
                this.level = jsonObj.getInt("CURRENT_LEVEL")
                binding.coins.text = coins.toString()
                binding.kms.text= String.format("%.2f",km)
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext,error.message.toString(),Toast.LENGTH_SHORT).show()
            }) {

        }
        queue.add(request)
    }

    fun getCoinsCount() : Int
    {
        return this.coins
    }
    fun getKms() : Double
    {
        return this.km
    }
    fun getCurrentLevel() : Int
    {
        return this.level
    }
    fun setCurrentLevel(level : Int)
    {
        this.level = level
    }
    fun setCoinsCount(newCoins:Int)
    {
        coins = newCoins
        binding.coins.text = coins.toString()
    }

    fun addCoinsCount(newCoins:Int)
    {
        coins += newCoins
        binding.coins.text = coins.toString()
    }

    fun setKmsCount(newKms : Double)
    {
        km = newKms
        binding.kms.text= String.format("%.2f",km)
    }

    fun addKmsCount (newKms : Double)
    {
        km  += newKms
        binding.kms.text= String.format("%.2f",km)
    }

    fun enableNavigation()
    {
        binding.viewPager.enableSwiping()
        binding.tabs.isEnabled = true
        binding.tabs.visibility = View.VISIBLE
    }

    fun disableNavigation()
    {
        binding.viewPager.disableSwiping()
        binding.tabs.isEnabled = false
        binding.tabs.visibility = View.GONE
    }


}