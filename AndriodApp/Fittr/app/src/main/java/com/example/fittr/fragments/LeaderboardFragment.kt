package com.example.fittr.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.MainActivity
import com.example.fittr.databinding.FragmentLeaderboardBinding
import com.example.fittr.dtos.User
import com.example.fittr.fragments.adapters.HistoryAdapter
import com.example.fittr.fragments.adapters.LeaderboardAdapter
import com.example.fittr.util.Variables
import org.json.JSONArray
import org.json.JSONObject
import java.util.logging.Level

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter : LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation= RecyclerView.VERTICAL
        binding.recycler.layoutManager=layoutManager
        binding.recycler.canScrollVertically(1)


        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when(position)
                {
                    0 -> {
                        getLeaderBoards("WALKING")
                    }
                    1 -> {
                        getLeaderBoards("RUNNING")
                    }
                    2 -> {
                        getLeaderBoards("CYCLING")
                    }
                    else -> {}
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }

            fun getLeaderBoards(mode : String)
            {
                val queue = Volley.newRequestQueue(context)
                var url="${Variables.url}/leaderBoard/${mode}"
                val builder = Uri.parse(url).buildUpon()
                val params = mapOf<String, Any>()
                val jsonObject = JSONObject(params)

                val request = object : JsonObjectRequest(
                    Request.Method.GET, builder.toString(), jsonObject,
                    Response.Listener { response ->
                        var strResp = response.toString()
                        val jsonObj: JSONObject = JSONObject(strResp)
                        val jsonArray: JSONArray = jsonObj.getJSONArray( "LEADERBOARD")
                        var usersActivity = arrayListOf<User>()

                        for(i in 0..jsonArray.length()-1)
                        {

                            var jsonObj=jsonArray.getJSONObject(i)
                            var name = jsonObj.getString("name")
                            var distance = jsonObj.getDouble("distanceTravelled")
                            var level = jsonObj.getInt("level")
                            usersActivity.add(User(Image = "" , Name = name , Level = level , Kms = distance))
                        }
                        adapter = LeaderboardAdapter(requireContext(),usersActivity)
                        binding.recycler.adapter=adapter
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(context,"Network Failure", Toast.LENGTH_SHORT).show()
                    }) {
                }
                queue.add(request)
            }

        }




    }
}