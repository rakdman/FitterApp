package com.example.fittr.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.MainActivity

import com.example.fittr.databinding.FragmentHistoryBinding
import com.example.fittr.dtos.Trip
import com.example.fittr.fragments.adapters.HistoryAdapter
import com.example.fittr.util.Variables
import org.json.JSONArray
import org.json.JSONObject


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tripHistory(Variables.userId!!)
    }

    fun tripHistory(userId:Int)
    {
        val queue = Volley.newRequestQueue(context)
        var url="${Variables.url}/api/tripDetails/${userId}"
        val builder = Uri.parse(url).buildUpon()
        val params = mapOf<String, Any>()
        val jsonObject = JSONObject(params)

        val request = object : JsonObjectRequest(
            Request.Method.GET, builder.toString(), jsonObject,
            Response.Listener { response ->

                var tripsArray = arrayListOf<Trip>()
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray( "TRIP_DETAILS")
                for(i in 0..jsonArray.length()-1)
                {
                    var jsonObj=jsonArray.getJSONObject(i)
                    var distance = jsonObj.getDouble("distanceTravelled")
                    var coins = jsonObj.getInt("coins")
                    var mode = jsonObj.getString("mode")
                    var activity = BikeFragment.activityEnum.UNKNOWN
                    when(mode)
                    {
                        "WALKING" -> {activity = BikeFragment.activityEnum.WALK}
                        "RUNNING" -> {activity = BikeFragment.activityEnum.RUN}
                        "CYCLING" -> {activity = BikeFragment.activityEnum.CYCLE}
                        "UNKNOWN" -> {activity = BikeFragment.activityEnum.UNKNOWN}
                    }
                    tripsArray.add(Trip(km = distance,coins = coins, activity = activity))
                }

                val adapter = HistoryAdapter(requireContext(),tripsArray)
                binding.recycler.adapter=adapter
                val layoutManager = LinearLayoutManager(requireContext())
                layoutManager.orientation= RecyclerView.VERTICAL
                binding.recycler.layoutManager=layoutManager
                binding.recycler.canScrollVertically(1)

            },
            Response.ErrorListener { error ->
                Toast.makeText(context,"Network Error", Toast.LENGTH_SHORT).show()
            }) {

        }
        queue.add(request)
    }

    override fun onResume() {
        super.onResume()
        tripHistory(Variables.userId!!)
    }

}