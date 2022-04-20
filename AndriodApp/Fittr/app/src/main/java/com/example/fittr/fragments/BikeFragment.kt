package com.example.fittr.fragments

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import br.com.goncalves.pugnotification.notification.PugNotification
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.MainActivity
import com.example.fittr.R
import com.example.fittr.databinding.FragmentBikeBinding
import com.example.fittr.dtos.Location
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import com.example.fittr.service.DetectionsService
import com.example.fittr.util.Variables
import com.google.android.gms.location.*
import kotlinx.coroutines.processNextEventInCurrentThread
import org.json.JSONObject


class BikeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    val REQUEST_CODE_PERMISSIONS = 123

    val MIN_CONFIDENCE = 50

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private var previousLocation : Location? = null

    var totalKms : Double = 0.0
    var totalCoins : Int = 0

    var isPaused = false


    enum class status {
        RUNNING,PAUSED,STOPPED
    }

    enum class activityEnum{
        WALK,RUN,CYCLE,UNKNOWN
    }

    var currentStatus = status.STOPPED
    var activitySelected = activityEnum.UNKNOWN

    private var _binding: FragmentBikeBinding? = null
    private val binding get() = _binding!!

    lateinit var client: ActivityRecognitionClient

    companion object {
        var ins: BikeFragment? = null
        fun getInstance(): BikeFragment? {
            return ins
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ins=this

        client = ActivityRecognition.getClient(this.requireContext())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            if(!hasPermissions(this.requireContext()))
            {
                requestPermissions()
            }
            else
            {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                getLocationUpdates()
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBikeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.activityWalk.setOnClickListener {
            activitySelected = activityEnum.WALK
            binding.activityWalk.setBackgroundResource(R.drawable.activity_button_selected)
            binding.activityCycle.setBackgroundResource(R.drawable.activity_button_not_selected)
            binding.activityRun.setBackgroundResource(R.drawable.activity_button_not_selected)
        }

        binding.activityRun.setOnClickListener {
            activitySelected = activityEnum.RUN
            binding.activityRun.setBackgroundResource(R.drawable.activity_button_selected)
            binding.activityCycle.setBackgroundResource(R.drawable.activity_button_not_selected)
            binding.activityWalk.setBackgroundResource(R.drawable.activity_button_not_selected)
        }

        binding.activityCycle.setOnClickListener {
            activitySelected = activityEnum.CYCLE
            binding.activityCycle.setBackgroundResource(R.drawable.activity_button_selected)
            binding.activityWalk.setBackgroundResource(R.drawable.activity_button_not_selected)
            binding.activityRun.setBackgroundResource(R.drawable.activity_button_not_selected)
        }


        binding.controlButton.setOnClickListener {
            if (activitySelected == activityEnum.UNKNOWN) {
                Toast.makeText(context, "Please Select Activity", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                when (currentStatus) {
                    status.STOPPED -> {
                        binding.controlButton.setImageResource(R.drawable.ic_baseline_stop_24);
                        MainActivity.getInstance()?.disableNavigation()

                        currentStatus = status.RUNNING;
                        isPaused = false
                        val preferences: SharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context)

                        preferences.edit()
                            .putInt("Activity", fromSpinnertoActivity(activitySelected)).apply()

                        this.requireContext().startService(
                            Intent(
                                this.requireContext(),
                                DetectionsService::class.java
                            )
                        )
                        startLocationUpdates()
                        binding.activityWalk.isEnabled = false
                        binding.activityCycle.isEnabled = false
                        binding.activityRun.isEnabled = false
                        binding.confidenceLayout.visibility = View.VISIBLE
                        binding.kmsLayout.visibility = View.VISIBLE


                    }
                    status.PAUSED -> {
//                    binding.controlButton.setImageResource(R.drawable.ic_baseline_pause_24);
//                    currentStatus=status.RUNNING;
                    }
                    status.RUNNING -> {


                        binding.controlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        this.requireContext().stopService(Intent(this.requireContext(), DetectionsService::class.java))
                        stopLocationUpdates()
                        previousLocation = null

                        binding.controlButton.visibility = View.GONE

                        binding.activityPaused.visibility = View.INVISIBLE

                        endTrip(1)


                        binding.activityCycle.setBackgroundResource(R.drawable.activity_button_not_selected)
                        binding.activityWalk.setBackgroundResource(R.drawable.activity_button_not_selected)
                        binding.activityRun.setBackgroundResource(R.drawable.activity_button_not_selected)

                        binding.confidenceLayout.visibility = View.INVISIBLE
                        binding.kmsLayout.visibility = View.INVISIBLE


                    }
                }
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocationUpdates()
        startLocationUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            "You Need to Allow This Permission",
            REQUEST_CODE_PERMISSIONS,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION

        )
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun hasPermissions(context: Context): Boolean =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )



    fun changeState(activityType: String, transitionType: String) {
        this.run {
            binding.activity.text=activityType
            binding.transition.text=(transitionType+"%")

            if(transitionType.toInt()<MIN_CONFIDENCE)
            {
                isPaused = true
                binding.activityPaused.visibility = View.VISIBLE
            }
            else
            {
                isPaused = false
                binding.activityPaused.visibility = View.INVISIBLE
            }
        }
    }

    fun fromSpinnertoActivity (activity : activityEnum): Int
    {
        return when(activity)
        {
            activityEnum.WALK ->DetectedActivity.WALKING
            activityEnum.RUN->DetectedActivity.RUNNING
            activityEnum.CYCLE->DetectedActivity.ON_BICYCLE
            else -> DetectedActivity.UNKNOWN

        }
    }

    private fun getLocationUpdates()
    {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = 10f
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location = locationResult.lastLocation
                    if(previousLocation!=null)
                    {
                        if(!isPaused)
                        {
                            totalKms += calculateDistance(long1 = previousLocation!!.long, lat1 = previousLocation!!.lat ,
                                long2 = location.longitude ,lat2 = location.latitude )
                            binding.kmCount.setText(String.format("%.2f",totalKms))
                        }
                        previousLocation!!.long = location.longitude
                        previousLocation!!.lat = location.latitude
                    }
                    else
                    {
                        previousLocation = Location(long = location.longitude,lat = location.latitude)
                    }
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun calculateDistance(long1 : Double,lat1 : Double,long2 : Double,lat2 : Double) : Double
    {
        val long1R = Math.toRadians(long1);
        val long2R = Math.toRadians(long2);
        val lat1R = Math.toRadians(lat1);
        val lat2R = Math.toRadians(lat2);

        val dlon = long2R - long1R
        val dlat = lat2R - lat1R
        val a = (Math.pow(Math.sin(dlat / 2), 2.0)
                + (Math.cos(lat1R) * Math.cos(lat2R)
                * Math.pow(Math.sin(dlon / 2), 2.0)))

        val c = 2 * Math.asin(Math.sqrt(a))

        val r = 6371.0

        return c * r
    }

    private fun showActivityDetails()
    {
        val dialogFragment : DialogFragment = ActivityFinishFragment()
        dialogFragment.isCancelable = false
        dialogFragment.show(activity?.supportFragmentManager!!,"Activity Report")
    }

    fun showControlButton()
    {
        binding.controlButton.visibility = View.VISIBLE
    }

    fun resetAfterActivity()
    {
        MainActivity.getInstance()?.addKmsCount(totalKms)
        MainActivity.getInstance()?.addCoinsCount(this.totalCoins)
        totalKms = 0.0
        totalCoins = 0
        binding.kmCount.setText("0.00")
        currentStatus = status.STOPPED;
        binding.activityWalk.isEnabled = true
        binding.activityCycle.isEnabled = true
        binding.activityRun.isEnabled = true
    }

    fun endTrip(userId : Int)
    {
        val queue = Volley.newRequestQueue(context)
        var url="${Variables.url}/api/user/saveTrip"

        val builder = Uri.parse(url).buildUpon()
        val params = mapOf<String, Any>(
            "userId" to userId.toString(),
            "distanceTravelled" to (this.totalKms*1000).toString(),
            "mode" to
            when(activitySelected) {
                activityEnum.WALK -> "WALKING"
                activityEnum.CYCLE -> "CYCLING"
                activityEnum.RUN -> "RUNNING"
                else -> "UNKNOWN"
            }
        )
        val jsonObject = JSONObject(params)

        val request = object : JsonObjectRequest(
            Request.Method.POST, builder.toString(), jsonObject,
            Response.Listener { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                this.totalCoins = jsonObj.getInt("COINS")
                var level = jsonObj.getInt("LEVEL")
                MainActivity.getInstance()?.setCurrentLevel(level)
                showActivityDetails()

            },
            Response.ErrorListener { error ->


            }) {

        }
        queue.add(request)
    }


    fun getCurrentTripCoins() : Int
    {
        return this.totalCoins
    }

    fun getCurrentKms() : Double
    {
        return this.totalKms
    }


    override fun onDestroyView() {
        super.onDestroyView()
        this.requireContext().stopService(Intent(this.requireContext(), DetectionsService::class.java))
        stopLocationUpdates()
    }


}