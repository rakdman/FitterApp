package com.example.fittr.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.fittr.databinding.FragmentSettingsBinding

import com.example.fittr.service.DetectionsService
import com.google.android.gms.location.ActivityRecognition
import android.content.SharedPreferences
import android.net.Uri
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.MainActivity
import com.example.fittr.R
import com.example.fittr.authentication.Login
import com.example.fittr.util.Variables
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutBtn.setOnClickListener {
            val intent = Intent(context, Login::class.java)
            startActivity(intent)
            getActivity()?.finish()
        }

        binding.deleteAccount.setOnClickListener {
            val ins = MainActivity.getInstance()
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Delete User")
            builder.setIcon(R.drawable.cyclist)
            builder.setMessage("Are you sure you want to delete your account")
            builder.setPositiveButton("Delete") { dialog, which ->
                deleteAccount(2)
                val intent = Intent(context, Login::class.java)
                startActivity(intent)
                getActivity()?.finish()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->

            }
            builder.show()

        }

        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {binding.nightVision.isChecked=true}
            Configuration.UI_MODE_NIGHT_NO -> {binding.nightVision.isChecked=false}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }


        binding.nightVision.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                preferences.edit().putInt("DarkMode",1).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                preferences.edit().putInt("DarkMode",0).apply()
            }
        }

    }

    fun deleteAccount(userId : Int)
    {
        val queue = Volley.newRequestQueue(context)
        var url="${Variables.url}/api/removeUser/${userId}"

        val builder = Uri.parse(url).buildUpon()
        val params = mapOf<String, Any>()
        val jsonObject = JSONObject(params)

        val request = object : JsonObjectRequest(
            Request.Method.DELETE, builder.toString(), jsonObject,
            Response.Listener { response ->


            },
            Response.ErrorListener { error ->


            }) {

        }
        queue.add(request)
    }



}