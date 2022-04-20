package com.example.fittr.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.MainActivity
import com.example.fittr.authentication.Login
import com.example.fittr.databinding.FragmentChangepasswordBinding
import com.example.fittr.util.Variables
import org.json.JSONObject

class ChangePassword : DialogFragment() {
    private var _binding: FragmentChangepasswordBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentChangepasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun changePassword(userId : Int,oldPassword:String, newPassword:String)
    {
        val queue = Volley.newRequestQueue(context)
        var url="${Variables.url}/updateuser/${userId}"
        val builder = Uri.parse(url).buildUpon()
        val params = mapOf<String, Any>(
            "password" to newPassword
        )
        val jsonObject = JSONObject(params)

        val request = object : JsonObjectRequest(
            Request.Method.PATCH, builder.toString(), jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                Variables.userId = null
                val intent = Intent(context,Login::class.java)
                startActivity(intent)
                dismiss()
            },
            Response.ErrorListener { error ->
                Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
                dismiss()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers[""] = ""
                return headers
            }

        }
        queue.add(request)
    }
}