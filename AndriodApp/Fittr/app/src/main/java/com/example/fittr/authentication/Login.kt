package com.example.fittr.authentication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.MainActivity
import com.example.fittr.databinding.ActivityLoginBinding
import com.example.fittr.util.Variables
import org.json.JSONObject


private lateinit var binding: ActivityLoginBinding

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.loginBtn.setOnClickListener {

            this.login(binding.emailField.text.toString(),binding.passwordField.text.toString())

        }
        binding.registerBtn.setOnClickListener {
            // Redirect to Register Page
            val intent = Intent(applicationContext, Register::class.java)
            startActivity(intent)
        }
    }

    fun login(email:String,password:String)
    {
        val queue = Volley.newRequestQueue(applicationContext)
        var url="${Variables.url}/login"
        val builder = Uri.parse(url).buildUpon()
        val params = mapOf<String, Any>(
            "email" to email,
            "password" to password
        )
        val jsonObject = JSONObject(params)

        val request = object : JsonObjectRequest(
            Request.Method.POST, builder.toString(), jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                Variables.setUserId(jsonObj.getInt("userId"))
                Variables.email = email
                Variables.password = password
                val intent = Intent(applicationContext,MainActivity::class.java)
                startActivity(intent)
                finish()

            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext,"Login Failed", Toast.LENGTH_SHORT).show()
            }) {

        }
        queue.add(request)
    }
}